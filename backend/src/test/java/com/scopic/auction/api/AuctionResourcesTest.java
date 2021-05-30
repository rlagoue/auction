package com.scopic.auction.api;

import com.scopic.auction.domain.InvalidNewMaxBidAmountException;
import com.scopic.auction.dto.SettingsDto;
import com.scopic.auction.service.AuctionService;
import com.scopic.auction.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
class AuctionResourcesTest extends BaseResourcesTest {

    private AuctionResources objectToTest;
    @Mock
    private AuctionService auctionService;
    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        objectToTest = new AuctionResources(auctionService, userService);
    }

    @Test
    void makeABidTest() {
        final String itemId = "itemId";
        var bid = 10d;

        final String expectedResponse = "success";
        Mockito.when(auctionService.makeABid(itemId, bid, CURRENT_USER)).thenReturn(expectedResponse);
        final String response = objectToTest.makeABid(itemId, bid);

        assertEquals(expectedResponse, response);
    }


    @Test
    void getSettingsTest() {
        final String username = "username";
        SettingsDto expected = Mockito.mock(SettingsDto.class);

        Mockito.when(userService.getSettings(username)).thenReturn(expected);

        final SettingsDto settingsDto = objectToTest.getSettings(username);

        assertSame(expected, settingsDto);
    }

    @Test
    void updateSettingsTest() throws InvalidNewMaxBidAmountException {
        final String username = "username";
        final SettingsDto data = Mockito.mock(SettingsDto.class);

        assertEquals("success", objectToTest.updateSettings(username, data));

        Mockito.verify(userService).updateSettings(username, data);
    }

    @Test
    void updateSettingsWhenFailingTest() throws InvalidNewMaxBidAmountException {
        final String username = "username";
        final SettingsDto data = Mockito.mock(SettingsDto.class);

        final var anyMessage = "anyMessage";
        Mockito.doThrow(new InvalidNewMaxBidAmountException(anyMessage)).when(userService).updateSettings(username, data);

        assertEquals(anyMessage, objectToTest.updateSettings(username, data));

        Mockito.verify(userService).updateSettings(username, data);
    }

    @Test
    void activateAutoBidOnItemTest() {
        final String itemId = UUID.randomUUID().toString();

        objectToTest.activateAutoBidOnItem(itemId);

        Mockito.verify(userService).activateAutoBidOnItem(CURRENT_USER, itemId);
    }

    @Test
    void deactivateAutoBidOnItemTest() {
        final String itemId = UUID.randomUUID().toString();

        objectToTest.deactivateAutoBidOnItem(itemId);

        Mockito.verify(userService).deactivateAutoBidOnItem(CURRENT_USER, itemId);
    }
}