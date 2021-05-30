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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    private InventoryService objectToTest;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService settingsRepository;

    @BeforeEach
    void setUp() {
        objectToTest = new InventoryService(itemRepository, settingsRepository);
    }

    @Test
    void getItemsTest() {
        final int pageIndex = 1;
        List<Item> items = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            final Item item = Mockito.mock(Item.class);
            Mockito.when(item.toDto()).thenReturn(Mockito.mock(ItemDto.class));
            items.add(item);
        }

        PageRequest pageable = Mockito.mock(PageRequest.class);

        try (final MockedStatic<PageRequest> mockedStaticPageRequest = Mockito.mockStatic(
                PageRequest.class
        )) {
            mockedStaticPageRequest.when(() -> PageRequest.of(
                    pageIndex,
                    10,
                    Sort.by(Sort.Direction.DESC, "time")
            )).thenReturn(pageable);


            final Page<Item> page = Mockito.mock(Page.class);
            final long totalCount = 100L;
            Mockito.when(page.getTotalElements()).thenReturn(totalCount);
            Mockito.when(page.getContent()).thenReturn(items);

            Mockito.when(itemRepository.findAll(pageable)).thenReturn(page);

            final User settings = Mockito.mock(User.class);
            final String username = "user1";
            ThreadLocalStorage.set(new ThreadLocalStorage(username));
            Mockito.when(settingsRepository.getById(username)).thenReturn(settings);
            Mockito.when(settings.isAutoBidActiveFor(Mockito.any(Item.class))).thenReturn(true);

            final ItemFetchDto itemFetchDto = objectToTest.getItems(pageIndex);

            assertEquals(totalCount, itemFetchDto.totalCount);
            assertEquals(10, itemFetchDto.items.size());
            assertTrue(itemFetchDto.items.stream().allMatch(item -> item != null));
            assertTrue(itemFetchDto.items.stream().allMatch(item -> item.isAutoBidActive));
            Mockito.verify(settings, Mockito.times(10)).isAutoBidActiveFor(Mockito.any(Item.class));
        }
    }

    @Test
    void getItemByIdTest() {
        final UUID itemId = UUID.randomUUID();
        final String itemIdAsString = itemId.toString();

        final User user = Mockito.mock(User.class);
        final String username = "user1";
        ThreadLocalStorage.set(new ThreadLocalStorage(username));
        Mockito.when(settingsRepository.getById(username)).thenReturn(user);
        Mockito.when(user.isAutoBidActiveFor(Mockito.any(Item.class))).thenReturn(true);

        Item item = Mockito.mock(Item.class);
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        ItemDto expectedItemDto = Mockito.mock(ItemDto.class);
        Mockito.when(item.toDto()).thenReturn(expectedItemDto);

        final ItemDto itemDto = objectToTest.getItemById(itemIdAsString);

        assertSame(expectedItemDto, itemDto);
        assertTrue(itemDto.isAutoBidActive);
    }
}