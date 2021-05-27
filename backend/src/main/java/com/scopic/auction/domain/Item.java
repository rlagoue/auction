package com.scopic.auction.domain;

import com.scopic.auction.dto.ItemDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "t_item")
public class Item {
    @Id
    @Column(name = "c_id")
    private long id;
    @Column(name = "c_name")
    private String name;
    @Column(name = "c_description")
    private String description;
    @OneToMany(mappedBy = "item")
    private final List<Bid> bids = new ArrayList<>();

    public Item() {
    }

    public Item(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Item(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public ItemDto toDto() {
        final ItemDto result = new ItemDto(id, name, description);
        result.bids = this.bids.stream()
                .map(bid -> bid.toDto())
                .collect(Collectors.toList());
        return result;
    }
}
