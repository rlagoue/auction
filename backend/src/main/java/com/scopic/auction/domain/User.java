package com.scopic.auction.domain;

import com.scopic.auction.dto.SettingsDto;
import com.scopic.auction.dto.UserDto;
import com.scopic.auction.repository.jpa.MoneyConverter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
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
    private final Set<Item> autoBidItems = new HashSet<>();
    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "t_leadingBids",
            joinColumns = @JoinColumn(name = "c_user"),
            inverseJoinColumns = @JoinColumn(name = "c_bid")
    )
    private final Set<Bid> leadingBids = new HashSet<>();

    public User() {
    }

    public User(String username) {
        this(username, Money.ZERO_USD);
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

    public Collection<Bid> update(Money maxBidAmount) throws InvalidNewMaxBidAmountException {
        if (getAutoBidTotalEngagement().isBiggerThan(maxBidAmount)) {
            throw new InvalidNewMaxBidAmountException(
                    "newMaxBidAmountSmallerThanCurrentEngagement"
            );
        }
        this.maxBidAmount = maxBidAmount;
        return bidObservedItems();
    }

    private Collection<Bid> bidObservedItems() {
        return autoBidItems.stream()
                .filter(item -> leadingBids.stream().noneMatch(bid -> bid.isAbout(item)))
                .flatMap(item -> item.tryAutoBidFor(this).stream())
                .collect(Collectors.toSet());
    }

    public Collection<Bid> activateAutoBidOn(Item item) {
        if (this.autoBidItems.contains(item)) {
            return Collections.emptyList();
        }
        if (leadingBids.stream().anyMatch(bid -> bid.isAbout(item))) {
            throw new UnsupportedOperationException("CannotActivateAutoBidWhenBeingLeadingBidder");
        }
        this.autoBidItems.add(item);
        return item.tryAutoBidFor(this);
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
                        .filter(bid -> this.autoBidItems.stream().anyMatch(bid::isAbout))
                        .collect(Collectors.toSet())
        );
    }

    public boolean isAutoBidActiveFor(Item item) {
        return this.autoBidItems.contains(item);
    }

    public Bid makeManualBid(Item item, Money amount, Optional<User> currentBidder) {
        if (this.autoBidItems.contains(item)) {
            throw new UnsupportedOperationException("CannotBidManuallyOnItemWithAutoBidActivated");
        }
        return registerNewBid(item, amount, currentBidder);
    }

    private Bid registerNewBid(Item item, Money amount, Optional<User> currentBidder) {
        return registerBid(new Bid(item, this, LocalDateTime.now(), amount), item, currentBidder);
    }

    private Bid registerBid(Bid bid, Item item, Optional<User> currentBidder) {
        this.leadingBids.add(bid);
        currentBidder.ifPresent(
                bidder -> bidder.leadingBids.removeIf(
                        candidate -> candidate.isAbout(item)
                )
        );
        return bid;
    }

    public Collection<Bid> tryToOutbidOn(Item item, Optional<User> currentLeadingBidder) {
        if (!this.autoBidItems.contains(item)) {
            return Collections.emptyList();
        }
        final var newBidderAvailableCash = computeAvailableCashFor(item);
        if (item.isCurrentBidBiggerThan(newBidderAvailableCash)) {
            return Collections.emptyList();
        }
        List<Bid> result = new ArrayList<>(2);
        if (!currentLeadingBidder.map(candidate -> candidate.isAutoBiddingOn(item)).orElse(false)) {
            final var newBid = currentLeadingBidder.flatMap(
                    candidate -> candidate.leadingBids
                            .stream()
                            .filter(bid -> bid.isAbout(item))
                            .findFirst()
            )
                    .map(bid -> bid.incrementFor(this))
                    .orElse(new Bid(item, this, LocalDateTime.now(), new Money(1, "USD")));
            result.add(registerBid(newBid, item, currentLeadingBidder));
        } else {
            final var currentLeadingBidderInstance = currentLeadingBidder.get();
            final var currentLeadingBidderAvailableCash = currentLeadingBidderInstance.computeAvailableCashFor(item);
            User winner;
            User looser;
            Money amount;
            if (newBidderAvailableCash.isBiggerThan(currentLeadingBidderAvailableCash)) {
                winner = this;
                looser = currentLeadingBidderInstance;
                amount = currentLeadingBidderAvailableCash;
            } else {
                winner = currentLeadingBidderInstance;
                looser = this;
                amount = newBidderAvailableCash;
                result.add(registerNewBid(item, newBidderAvailableCash, Optional.of(winner)));
            }
            result.add(winner.registerNewBid(item, amount.nextAmount(), Optional.of(looser)));
        }
        return result;
    }

    private boolean isAutoBiddingOn(Item item) {
        return this.autoBidItems.contains(item);
    }

    public void deactivateAutoBidOn(Item item) {
        if (leadingBids.stream().anyMatch(bid -> bid.isAbout(item))) {
            throw new IllegalStateException("CannotDeactivateAutoBidOnItemWhenLeading");
        }
        autoBidItems.remove(item);
    }
}
