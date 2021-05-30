package com.scopic.auction.service;

import com.scopic.auction.api.BaseResourcesTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AuctionServiceTest extends BaseResourcesTest {

    private AuctionService objectToTest;
    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        objectToTest = new AuctionService(userService);
    }

    @Test
    void makeABidTest() {
        final String itemId = UUID.randomUUID().toString();
        String expected = "expected";
        final var bid = 10d;
        Mockito.when(userService.makeABid(itemId, bid, CURRENT_USER)).thenReturn(expected);

        final String response = objectToTest.makeABid(
                itemId,
                bid,
                CURRENT_USER
        );
        assertEquals(expected, response);
    }

    @Test
    void makeABidWithOriginalStateChangedTest() {
        final String itemId = UUID.randomUUID().toString();
        final var bid = 10d;
        Mockito.when(userService.makeABid(itemId, bid, CURRENT_USER))
                .thenThrow(
                        new ObjectOptimisticLockingFailureException(
                                "",
                                new Object()
                        )
                );

        final String response = objectToTest.makeABid(
                itemId,
                bid,
                CURRENT_USER
        );
        assertEquals("original-state-changed", response);
    }

}