package com.scopic.auction.domain;

import com.scopic.auction.dto.BidDto;
import com.scopic.auction.repository.jpa.MoneyConverter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_bid")
public class Bid {
    @Id
    @Column(name = "c_id")
    private long id;
    @ManyToOne
    @JoinColumn(name = "c_item", referencedColumnName = "c_id")
    private Item item;
    @Embedded
    @Column(name = "c_username")
    private User user;
    @Column(name = "c_time")
    private LocalDateTime time;
    @Basic
    @Column(name = "c_amount")
    @Convert(converter = MoneyConverter.class)
    private Money amount;

    public Bid() {
    }

    public Bid(Item item, User user, LocalDateTime time, Money amount) {
        this.item = item;
        this.user = user;
        this.time = time;
        this.amount = amount;
    }

    public BidDto toDto() {
        final BidDto result = new BidDto();
        result.id = this.id;
        result.user = this.user.toDto();
        result.time = this.time;
        result.amount = this.amount.toDto();
        return result;
    }
}
