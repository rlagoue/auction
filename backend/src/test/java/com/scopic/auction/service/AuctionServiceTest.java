package com.scopic.auction.service;

import com.scopic.auction.domain.Settings;
import com.scopic.auction.dto.MakeBidDto;
import com.scopic.auction.dto.SettingsDto;
import com.scopic.auction.repository.SettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
class AuctionServiceTest {

    private AuctionService objectToTest;
    @Mock
    private BidService bidService;
    @Mock
    private SettingsRepository settingsRepository;

    @BeforeEach
    void setUp() {
        objectToTest = new AuctionService(bidService, settingsRepository);
    }

    @Test
    void makeABidTest() {
        final String itemId = UUID.randomUUID().toString();
        String expected = "expected";
        final MakeBidDto data = Mockito.mock(MakeBidDto.class);
        Mockito.when(bidService.makeABid(itemId, data)).thenReturn(expected);

        final String response = objectToTest.makeABid(
                itemId,
                data
        );
        assertEquals(expected, response);
    }

    @Test
    void makeABidWithOriginalStateChangedTest() {
        final String itemId = UUID.randomUUID().toString();
        final MakeBidDto data = Mockito.mock(MakeBidDto.class);
        Mockito.when(bidService.makeABid(itemId, data))
                .thenThrow(
                        new ObjectOptimisticLockingFailureException(
                                "",
                                new Object()
                        )
                );

        final String response = objectToTest.makeABid(
                itemId,
                data
        );
        assertEquals("original-state-changed", response);
    }

    @Test
    void getSettingsTest() {
        final String username = "username";

        Settings settings = Mockito.mock(Settings.class);
        Mockito.when(settingsRepository.findById(username))
                .thenReturn(Optional.of(settings));
        SettingsDto expected = Mockito.mock(SettingsDto.class);
        Mockito.when(settings.toDto()).thenReturn(expected);

        final SettingsDto settingsDto = objectToTest.getSettings(username);

        assertSame(expected, settingsDto);
    }
}