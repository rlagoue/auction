package com.scopic.auction.dto;


import java.util.ArrayList;
import java.util.List;

public class ItemDto {
    public String id;
    public String name;
    public String description;
    public List<BidDto> bids = new ArrayList<>();
    public boolean isAutoBidActive;

    public ItemDto() {
    }

    public ItemDto(String id) {
        this.id = id;
    }

    public ItemDto(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
