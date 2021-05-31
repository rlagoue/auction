package com.scopic.auction.domain;

import com.scopic.auction.dto.ItemDto;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "t_item")
public class Item extends BaseDomainObject {
    @OneToMany(mappedBy = "item")
    private final Set<Bid> bids = new HashSet<>();
    @Column(name = "c_name")
    private String name;
    @Column(name = "c_description")
    private String description;
    @ManyToOne
    @JoinColumn(name = "c_currentbidder", referencedColumnName = "c_username")
    private User leadingBidder;

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

    public Collection<Bid> makeManualBid(User newBidder, Money amount) {
        if (this.bids.stream().anyMatch(bid -> bid.isBiggerThan(amount))) {
            return Collections.emptyList();
        }
        Bid bid = newBidder.makeManualBid(
                this,
                amount,
                Optional.ofNullable(leadingBidder)
        );
        return registerNewBid(bid, newBidder);
    }

    Collection<Bid> registerNewBid(Bid bid, User newBidder) {
        Collection<Bid> newBids = new ArrayList<>(2);
        var previousLeadingBidder = this.leadingBidder;
        addNewBids(List.of(bid), newBidder, newBids);
        if (previousLeadingBidder != null) {
            final var bids = previousLeadingBidder.tryToOutbidOn(this, Optional.of(newBidder));
            addNewBids(bids, previousLeadingBidder, newBids);
        }
        return newBids;
    }

    private Collection<Bid> addNewBids(Collection<Bid> bids, User newLeadBidder, Collection<Bid> newBids) {
        if (!bids.isEmpty()) {
            this.leadingBidder = newLeadBidder;
        }
        this.bids.addAll(bids);
        newBids.addAll(bids);
        return bids;
    }

    public Collection<Bid> tryAutoBidFor(User newBidder) {
        final var bids = newBidder.tryToOutbidOn(
                this,
                Optional.ofNullable(this.leadingBidder)
        );
        return addNewBids(bids, newBidder, new ArrayList<>());
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
