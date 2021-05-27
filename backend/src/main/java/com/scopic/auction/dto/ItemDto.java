package com.scopic.auction.dto;

public class ItemDto {
    public long id;
    public String name;
    public String description;

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
