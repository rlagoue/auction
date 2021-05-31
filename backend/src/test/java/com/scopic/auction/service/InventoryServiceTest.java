package com.scopic.auction.service;

import com.scopic.auction.domain.Item;
import com.scopic.auction.domain.User;
import com.scopic.auction.dto.ItemDto;
import com.scopic.auction.dto.ItemFetchDto;
import com.scopic.auction.repository.ItemRepository;
import com.scopic.auction.utils.ThreadLocalStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    private InventoryService objectToTest;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        objectToTest = new InventoryService(itemRepository, userService);
    }

    @Test
    void getItemsTest() {
        final int pageIndex = 1;
        List<Item> items = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            final Item item = mock(Item.class);
            when(item.toDto()).thenReturn(mock(ItemDto.class));
            items.add(item);
        }

        PageRequest pageable = mock(PageRequest.class);

        try (final MockedStatic<PageRequest> mockedStaticPageRequest = Mockito.mockStatic(
                PageRequest.class
        )) {
            mockedStaticPageRequest.when(() -> PageRequest.of(
                    pageIndex,
                    10,
                    Sort.by(Sort.Direction.DESC, "name")
            )).thenReturn(pageable);


            final Page<Item> page = mock(Page.class);
            final long totalCount = 100L;
            when(page.getTotalElements()).thenReturn(totalCount);
            when(page.getContent()).thenReturn(items);

            when(itemRepository.findAll(pageable)).thenReturn(page);

            final User settings = mock(User.class);
            final String username = "user1";

            when(userService.getById(username)).thenReturn(settings);
            when(settings.isAutoBidActiveFor(Mockito.any(Item.class))).thenReturn(true);

            final ItemFetchDto itemFetchDto = objectToTest.getItems(username, pageIndex);

            assertEquals(totalCount, itemFetchDto.totalCount);
            assertEquals(10, itemFetchDto.items.size());
            assertTrue(itemFetchDto.items.stream().allMatch(Objects::nonNull));
            assertTrue(itemFetchDto.items.stream().allMatch(item -> item.isAutoBidActive));
            Mockito.verify(settings, Mockito.times(10)).isAutoBidActiveFor(Mockito.any(Item.class));
        }
    }

    @Test
    void getItemByIdTest() {
        final UUID itemId = UUID.randomUUID();
        final String itemIdAsString = itemId.toString();

        final User user = mock(User.class);
        final String username = "user1";
        ThreadLocalStorage.set(new ThreadLocalStorage(username));
        when(userService.getById(username)).thenReturn(user);
        when(user.isAutoBidActiveFor(Mockito.any(Item.class))).thenReturn(true);

        Item item = mock(Item.class);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        ItemDto expectedItemDto = mock(ItemDto.class);
        when(item.toDto()).thenReturn(expectedItemDto);

        final ItemDto itemDto = objectToTest.getItemById(username, itemIdAsString);

        assertSame(expectedItemDto, itemDto);
        assertTrue(itemDto.isAutoBidActive);
    }

    @Test
    void addItemTest() {
        final var name = "mug";
        final var description = "Njoya personal mug";
        final var data = new ItemDto();
        data.name = name;
        data.description = description;

        final var expectedItemId = UUID.randomUUID();

        try (final var itemMockedConstruction = Mockito.mockConstruction(
                Item.class,
                (mock, context) -> {
                    assertEquals(name, context.arguments().get(0));
                    assertEquals(description, context.arguments().get(1));
                    when(mock.getId()).thenReturn(expectedItemId);
                }
        )
        ) {
            assertEquals(expectedItemId, objectToTest.addItem(data));
            assertEquals(1, itemMockedConstruction.constructed().size());
            final var createdItem = itemMockedConstruction.constructed().get(0);
            Mockito.verify(itemRepository).saveAndFlush(createdItem);
        }

    }
}