package com.scopic.auction.service;

import com.scopic.auction.domain.Item;
import com.scopic.auction.dto.ItemFetchDto;
import com.scopic.auction.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class InventoryService {

    private final ItemRepository itemRepository;

    @Autowired
    public InventoryService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Transactional
    public ItemFetchDto getItems(int pageIndex) {
        final Page<Item> items = itemRepository.findAll(
                PageRequest.of(
                        pageIndex,
                        10,
                        Sort.by(Sort.Direction.DESC, "id")
                )
        );
        return new ItemFetchDto(
                items.getTotalElements(),
                items.getContent()
                        .stream()
                        .map(Item::toDto)
                        .collect(Collectors.toList())
        );
    }
}
