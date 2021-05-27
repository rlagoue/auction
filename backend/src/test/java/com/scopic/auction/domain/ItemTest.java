package com.scopic.auction.domain;

import com.scopic.auction.dto.ItemDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemTest {

    private Item objectToTest;

    @BeforeEach
    void setUp() {
        objectToTest = new Item();
    }

    @Test
    void toDtoTest() {
        final int id = 1;
        final String name = "name";
        final String description = "description";

        objectToTest = new Item(id, name, description);

        final ItemDto itemDto = objectToTest.toDto();

        assertEquals(id, itemDto.id);
        assertEquals(name, itemDto.name);
        assertEquals(description, itemDto.description);
    }
}