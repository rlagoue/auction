package com.scopic.auction.api;

import com.scopic.auction.dto.MakeBidDto;
import com.scopic.auction.dto.UserDto;
import com.scopic.auction.service.AuctionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AuctionResourcesTest {

    private AuctionResources objectToTest;
    @Mock
    private AuctionService auctionService;

    @BeforeEach
    void setUp() {
        objectToTest = new AuctionResources(auctionService);
    }

    @Test
    void makeABidTest() {
        final String itemId = "itemId";
        final MakeBidDto data = new MakeBidDto();
        data.user = new UserDto("user1");
        data.bid = 10;

        final String expectedResponse = "success";
        Mockito.when(auctionService.makeABid(itemId, data)).thenReturn(expectedResponse);
        final String response = objectToTest.makeABid(itemId, data);

        assertEquals(expectedResponse, response);
    }
}