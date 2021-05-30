package com.scopic.auction.domain;

import com.scopic.auction.dto.ItemDto;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "t_item")
public class Item extends BaseDomainObject {
    @Column(name = "c_name")
    private String name;
    @Column(name = "c_description")
    private String description;
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private Set<Bid> bids = new HashSet<>();
    @ManyToOne
    @JoinColumn(name = "c_currentbidder", referencedColumnName = "c_username")
    private User currentBidder;

    public Item() {
    }

    public Item(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public ItemDto toDto() {
        final ItemDto result = new ItemDto(id.toString(), name, description);
        result.bids = this.bids.stream()
                .map(bid -> bid.toDto())
                .collect(Collectors.toList());
        return result;
    }

    public void makeNextPossibleBid(User newBidder) {
        if (getNewestBid()
                .map(bid -> bid.incrementFor(newBidder))
                .map(bid -> registerNewBid(bid, newBidder))
                .isEmpty()
        ) {
            makeABid(newBidder, new Money(1, "USD"));
        }
    }

    public String makeABid(User newBidder, Money amount) {
        if (this.bids.stream().anyMatch(bid -> bid.isBiggerThan(amount))) {
            return "outbidded";
        }
        Bid bid = newBidder.makeABid(
                this,
                amount,
                Optional.ofNullable(currentBidder)
        );
        return registerNewBid(bid, newBidder);
    }

    String registerNewBid(Bid bid, User newBidder) {
        this.bids.add(bid);
        Optional<Bid> outbid = tryAutoBid(newBidder);
        outbid.ifPresent(newBid -> this.bids.add(newBid));
        return outbid.isEmpty() ? "success" : "outbidded";
    }

    private Optional<Bid> tryAutoBid(User newBidder) {
        if (this.currentBidder == null) {
            this.currentBidder = newBidder;
            return Optional.empty();
        } else {
            return this.currentBidder.tryToOutbidOn(this, newBidder);
        }
    }

    public boolean isCurrentBidBiggerThan(Money amount) {
        final var newestBid = getNewestBid();
        return newestBid.map(bid -> bid.isBiggerThan(amount)).orElse(false);
    }

    private Optional<Bid> getNewestBid() {
        return Bid.newestOf(this.bids);
    }

    public Money addWithCurrentBid(Money amount) {
        return getNewestBid().map(bid -> bid.addWithAmount(amount)).orElse(amount);
    }

}
