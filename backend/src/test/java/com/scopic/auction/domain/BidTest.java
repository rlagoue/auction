package com.scopic.auction.domain;

import com.scopic.auction.dto.BidDto;
import com.scopic.auction.dto.MoneyDto;
import com.scopic.auction.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static com.scopic.auction.utils.Whitebox.getFieldValue;
import static com.scopic.auction.utils.Whitebox.setFieldValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class BidTest {

    private Bid objectToTest;

    @BeforeEach
    void setUp() {
        objectToTest = new Bid();
    }

    @Test
    void bidTest() {
        Item item = Mockito.mock(Item.class);
        User user = Mockito.mock(User.class);
        LocalDateTime time = LocalDateTime.now();
        Money amount = Mockito.mock(Money.class);

        objectToTest = new Bid(item, user, time, amount);

        assertSame(item, getFieldValue(objectToTest, "item"));
        assertSame(user, getFieldValue(objectToTest, "user"));
        assertSame(time, getFieldValue(objectToTest, "time"));
        assertSame(amount, getFieldValue(objectToTest, "amount"));
    }

    @Test
    void toDtoTest() {
        User user = Mockito.mock(User.class);
        Item item = Mockito.mock(Item.class);
        final LocalDateTime time = LocalDateTime.now();
        final long id = 1L;
        Money amount = Mockito.mock(Money.class);
        setFieldValue(objectToTest, "id", id);
        setFieldValue(objectToTest, "user", user);
        setFieldValue(objectToTest, "item", item);
        setFieldValue(objectToTest, "time", time);
        setFieldValue(objectToTest, "amount", amount);

        UserDto userDto = Mockito.mock(UserDto.class);
        Mockito.when(user.toDto()).thenReturn(userDto);
        MoneyDto amountDto = Mockito.mock(MoneyDto.class);
        Mockito.when(amount.toDto()).thenReturn(amountDto);

        final BidDto bidDto = objectToTest.toDto();

        assertEquals(id, bidDto.id);
        assertEquals(userDto, bidDto.user);
        assertEquals(time, bidDto.time);
        assertEquals(amountDto, bidDto.amount);

        Mockito.verify(item, Mockito.never()).toDto();
    }
}