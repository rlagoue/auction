package com.scopic.auction.dto;


import java.util.ArrayList;
import java.util.List;

public class ItemDto {
    public long id;
    public String name;
    public String description;
    public List<BidDto> bids = new ArrayList<>();

    public ItemDto() {
    }

    public ItemDto(long id) {
        this.id = id;
    }

    public ItemDto(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
