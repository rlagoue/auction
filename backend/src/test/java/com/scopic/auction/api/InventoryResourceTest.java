package com.scopic.auction.api;

import com.scopic.auction.dto.ItemDto;
import com.scopic.auction.dto.ItemFetchDto;
import com.scopic.auction.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InventoryResourceTest {

    private InventoryResource objectToTest;
    @Mock
    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        objectToTest = new InventoryResource(inventoryService);
    }

    @Test
    void getItemsTest() {
        long itemId1 = 1;
        long itemId2 = 2;

        final int pageIndex = 1;
        final int totalCount = 100;
        List<ItemDto> items = new ArrayList<>();
        for (int i =1 ; i <= 10; i++) {
            items.add(new ItemDto(i));
        }
        Mockito.when(inventoryService.getItems(pageIndex))
                .thenReturn(new ItemFetchDto(totalCount, items));

        final ItemFetchDto itemFetchDto = objectToTest.getItems(pageIndex);

        assertEquals(totalCount, itemFetchDto.totalCount);
        assertEquals(10, itemFetchDto.items.size());
        assertTrue(itemFetchDto.items.stream().anyMatch(item -> item.id == itemId1));
        assertTrue(itemFetchDto.items.stream().anyMatch(item -> item.id == itemId2));
    }

    @Test
    void getItemByIdTest() {
        final long itemId = 1;

        ItemDto expectedItemDto = new ItemDto();
        expectedItemDto.id = itemId;

        Mockito.when(inventoryService.getItemById(itemId)).thenReturn(expectedItemDto);

        final ItemDto itemDto = objectToTest.getItemById(itemId);

        assertEquals(itemId, itemDto.id);
    }
}