package com.scopic.auction.domain;

import com.scopic.auction.dto.BidDto;
import com.scopic.auction.repository.jpa.MoneyConverter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Entity
@Table(name = "t_bid")
public class Bid extends BaseDomainObject {
    @ManyToOne
    @JoinColumn(name = "c_item", referencedColumnName = "c_id")
    private Item item;
    @ManyToOne
    @JoinColumn(
            name = "c_bidder",
            referencedColumnName = "c_username"
    )
    private User bidder;
    @Column(name = "c_time")
    private LocalDateTime time;
    @Basic
    @Column(name = "c_amount")
    @Convert(converter = MoneyConverter.class)
    private Money amount;

    public Bid() {
    }

    public Bid(Item item, User bidder, LocalDateTime time, Money amount) {
        this.item = item;
        this.bidder = bidder;
        this.time = time;
        this.amount = amount;
    }

    public static Money sum(Collection<Bid> bids) {
        return bids.stream()
                .map(bid -> bid.amount)
                .reduce(
                        new Money(0, "USD"),
                        (a, b) -> a.add(b)
                );
    }

    public static Optional<Bid> newestOf(Collection<Bid> bids) {
        return bids.stream()
                .sorted((a, b) -> b.time.compareTo(a.time))
                .limit(1)
                .findFirst();
    }

    public BidDto toDto() {
        final BidDto result = new BidDto();
        result.id = this.id.toString();
        result.user = this.bidder.toDto();
        result.time = this.time;
        result.amount = this.amount.toDto();
        return result;
    }

    public boolean isBiggerThan(Money amount) {
        return this.amount.isBiggerThan(amount);
    }

    public boolean isAbout(Item item) {
        return this.item.equals(item);
    }

    public Bid incrementFor(User bidder) {
        Money nextAmount = this.amount.nextAmount();
        return new Bid(item, bidder, LocalDateTime.now(), nextAmount);
    }

    public Money addWithAmount(Money amount) {
        return this.amount.add(amount);
    }
}
