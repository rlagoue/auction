package com.scopic.auction.dto;

import java.util.List;

public class ItemFetchDto {

    public long totalCount;
    public List<ItemDto> items;

    public ItemFetchDto() {
    }

    public ItemFetchDto(long totalCount, List<ItemDto> items) {
        this.totalCount = totalCount;
        this.items = items;
    }
}
