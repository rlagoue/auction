package com.scopic.auction.service;

import com.scopic.auction.domain.Item;
import com.scopic.auction.domain.User;
import com.scopic.auction.dto.ItemDto;
import com.scopic.auction.dto.ItemFetchDto;
import com.scopic.auction.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class InventoryService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Autowired
    public InventoryService(ItemRepository itemRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Transactional
    public ItemFetchDto getItems(String currentUserId, int pageIndex) {
        final Page<Item> items = itemRepository.findAll(
                PageRequest.of(
                        pageIndex,
                        10,
                        Sort.by(Sort.Direction.DESC, "name")
                )
        );
        final var currentUser = userService.getById(currentUserId);
        return new ItemFetchDto(
                items.getTotalElements(),
                items.getContent()
                        .stream()
                        .map(new ItemToDto(currentUser))
                        .collect(Collectors.toList())
        );
    }

    @Transactional
    public ItemDto getItemById(String currentUserId, String itemId) {
        return itemRepository.findById(UUID.fromString(itemId))
                .map(new ItemToDto(userService.getById(currentUserId)))
                .orElse(null);
    }

    @Transactional
    public UUID addItem(ItemDto data) {
        final var item = new Item(data.name, data.description);
        itemRepository.saveAndFlush(item);
        return item.getId();
    }

    private static class ItemToDto implements Function<Item, ItemDto> {

        private final User user;

        ItemToDto(User user) {
            this.user = user;
        }

        @Override
        public ItemDto apply(Item item) {
            final ItemDto result = item.toDto();
            result.isAutoBidActive = user.isAutoBidActiveFor(item);
            return result;
        }
    }
}
