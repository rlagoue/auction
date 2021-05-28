package com.scopic.auction.api;

import com.scopic.auction.dto.MakeBidDto;
import com.scopic.auction.dto.UserDto;
import com.scopic.auction.service.BidService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class BidResourcesTest {

    private BidResources objectToTest;
    @Mock
    private BidService bidService;

    @BeforeEach
    void setUp() {
        objectToTest = new BidResources(bidService);
    }

    @Test
    void makeABidTest() {
        final String itemId = "itemId";
        final MakeBidDto data = new MakeBidDto();
        data.user = new UserDto("user1");
        data.bid = 10;

        final String expectedResponse = "success";
        Mockito.when(bidService.makeABid(itemId, data)).thenReturn(expectedResponse);
        final String response = objectToTest.makeABid(itemId, data);

        assertEquals(expectedResponse, response);
    }
}