package com.scopic.auction.domain;

import com.scopic.auction.dto.BidDto;
import com.scopic.auction.dto.ItemDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static com.scopic.auction.utils.Whitebox.setFieldValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        Bid bid1 = Mockito.mock(Bid.class);
        Bid bid2 = Mockito.mock(Bid.class);
        setFieldValue(objectToTest, "bids", List.of(bid1, bid2));

        BidDto bidDto1 =Mockito.mock(BidDto.class);
        BidDto bidDto2 =Mockito.mock(BidDto.class);
        Mockito.when(bid1.toDto()).thenReturn(bidDto1);
        Mockito.when(bid2.toDto()).thenReturn(bidDto2);

        final ItemDto itemDto = objectToTest.toDto();

        assertEquals(id, itemDto.id);
        assertEquals(name, itemDto.name);
        assertEquals(description, itemDto.description);
        assertEquals(2, itemDto.bids.size());
        assertTrue(itemDto.bids.contains(bidDto1));
        assertTrue(itemDto.bids.contains(bidDto2));
    }
}