package com.scopic.auction.domain;

import com.scopic.auction.dto.ItemDto;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "t_item")
public class Item extends BaseDomainObject {
    @OneToMany(mappedBy = "item")
    private final List<Bid> bids = new ArrayList<>();
    @Id
    @GeneratedValue
    @Column(name = "c_id")
    private UUID id;
    @Column(name = "c_name")
    private String name;
    @Column(name = "c_description")
    private String description;

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

    public Bid makeABid(User user, Money amount) {
        if (this.bids.stream().anyMatch(bid -> bid.isBiggerThan(amount))) {
            return null;
        }
        final Bid bid = new Bid(this, user, LocalDateTime.now(), amount);
        this.bids.add(bid);
        return bid;
    }

}
