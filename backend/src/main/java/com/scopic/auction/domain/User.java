package com.scopic.auction.domain;

import com.scopic.auction.dto.SettingsDto;
import com.scopic.auction.dto.UserDto;
import com.scopic.auction.repository.jpa.MoneyConverter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "t_user")
public class User {
    @Id
    @Column(name = "c_username")
    private String username;
    @Basic
    @Column(name = "c_maxbidamount")
    @Convert(converter = MoneyConverter.class)
    private Money maxBidAmount;
    @ManyToMany
    @JoinTable(
            name = "t_autobiditems",
            joinColumns = @JoinColumn(name = "c_user"),
            inverseJoinColumns = @JoinColumn(name = "c_item")
    )
    private Set<Item> autoBidItems = new HashSet<>();
    @OneToMany
    @JoinTable(
            name = "t_leadingBids",
            joinColumns = @JoinColumn(name = "c_user"),
            inverseJoinColumns = @JoinColumn(name = "c_bid")
    )
    private Set<Bid> leadingBids = new HashSet<>();

    public User() {
    }

    public User(String username) {
        this(username, new Money(0, "USD"));
    }

    public User(String username, Money maxBidAmount) {
        this.username = username;
        this.maxBidAmount = maxBidAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    public UserDto toDto() {
        return new UserDto(username);
    }

    public SettingsDto getSettings() {
        final SettingsDto result = new SettingsDto();
        result.maxBidAmount = this.maxBidAmount.toDto();
        return result;
    }

    public void update(Money maxBidAmount) throws InvalidNewMaxBidAmountException {
        if (!this.leadingBids.isEmpty() &&
                getAutoBidTotalEngagement().isBiggerThan(maxBidAmount)
        ) {
            throw new InvalidNewMaxBidAmountException(
                    "newMaxBidAmountSmallerThanCurrentEngagement"
            );
        }
        this.maxBidAmount = maxBidAmount;

        bidObservedItems();
    }

    private void bidObservedItems() {
        autoBidItems.stream()
                .filter(
                        item -> leadingBids.stream()
                                .noneMatch(bid -> bid.isAbout(item))
                )
                .forEach(item -> item.makeNextPossibleBid(this));
    }

    public void activateAutoBidOn(Item item) {
        if (this.autoBidItems.contains(item)) {
            return;
        }
        this.autoBidItems.add(item);
    }

    private Money computeAvailableCashFor(Item item) {
        final var availableCashIfNotLeadingOnItem = this.maxBidAmount.subtract(
                getAutoBidTotalEngagement()
        );
        if (this.leadingBids.stream().anyMatch(bid -> bid.isAbout(item))) {
            return item.addWithCurrentBid(availableCashIfNotLeadingOnItem);
        }
        return availableCashIfNotLeadingOnItem;
    }

    private Money getAutoBidTotalEngagement() {
        return Bid.sum(
                this.leadingBids.stream()
                        .filter(
                                bid -> this.autoBidItems.stream()
                                        .anyMatch(bid::isAbout)
                        )
                        .collect(Collectors.toSet())
        );
    }

    public boolean isAutoBidActiveFor(Item item) {
        return this.autoBidItems.contains(item);
    }

    public Bid makeABid(Item item, Money amount, Optional<User> currentBidder) {
        return registerNewBid(
                new Bid(item, this, LocalDateTime.now(), amount),
                item,
                currentBidder
        );
    }

    private Bid registerNewBid(Bid bid, Item item, Optional<User> currentBidder) {
        this.leadingBids.add(bid);
        currentBidder.ifPresent(
                bidder -> bidder.leadingBids.removeIf(
                        candidate -> candidate.isAbout(item)
                )
        );
        return bid;
    }

    public Optional<Bid> tryToOutbidOn(Item item, User currentLeadingBidder) {
        if (!this.autoBidItems.contains(item)) {
            return Optional.empty();
        }
        final var newBidderAvailableCash = computeAvailableCashFor(item);
        if (item.isCurrentBidBiggerThan(newBidderAvailableCash)) {
            return Optional.empty();
        }
        Bid result;
        if (!currentLeadingBidder.autoBidItems.contains(item)) {
            result = registerNewBid(
                    Bid.newestOf(currentLeadingBidder.leadingBids).orElseThrow().incrementFor(this),
                    item,
                    Optional.ofNullable(currentLeadingBidder)
            );
        } else {
            final var currentLeadingBidderAvailableCash = currentLeadingBidder.computeAvailableCashFor(item);
            User winner;
            User looser;
            Money amount;
            if (newBidderAvailableCash.isBiggerThan(currentLeadingBidderAvailableCash)) {
                winner = this;
                looser = currentLeadingBidder;
                amount = currentLeadingBidderAvailableCash;
            } else {
                winner = currentLeadingBidder;
                looser = this;
                amount = newBidderAvailableCash;
            }
            result = winner.makeABid(
                    item,
                    amount.nextAmount(),
                    Optional.of(looser)
            );
        }
        return Optional.of(result);
    }

    public void deactivateAutoBidOn(Item item) {
        if (leadingBids.stream().anyMatch(bid -> bid.isAbout(item))) {
            throw new IllegalStateException("CannotDeactivateAutoBidOnItemWhenLeading");
        }
        autoBidItems.remove(item);
    }
}
