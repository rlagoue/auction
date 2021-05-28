package com.scopic.auction.domain;

import com.scopic.auction.dto.MoneyDto;
import com.scopic.auction.dto.SettingsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static com.scopic.auction.utils.Whitebox.getFieldValue;
import static com.scopic.auction.utils.Whitebox.setFieldValue;
import static org.junit.jupiter.api.Assertions.*;

class SettingsTest {

    private Settings objectToTest;

    @BeforeEach
    void setUp() {
        objectToTest = new Settings();
    }

    @Test
    void settingsTest() {
        Money amount = Mockito.mock(Money.class);
        final String username = "username";
        objectToTest = new Settings(username, amount);

        assertSame(amount, getFieldValue(objectToTest, "maxBidAmount"));
        assertSame(username, getFieldValue(objectToTest, "username"));
    }

    @Test
    void toDtoTest() {
        final String username = "username";
        MoneyDto amountDto = Mockito.mock(MoneyDto.class);

        final Money amount = Mockito.mock(Money.class);
        Mockito.when(amount.toDto()).thenReturn(amountDto);
        setFieldValue(objectToTest, "username", username);
        setFieldValue(objectToTest, "maxBidAmount", amount);

        final SettingsDto dto = objectToTest.toDto();

        assertEquals(username, dto.username);
        assertEquals(amountDto, dto.maxBidAmount);
    }
}