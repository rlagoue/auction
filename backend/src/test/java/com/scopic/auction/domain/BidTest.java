package com.scopic.auction.domain;

import com.scopic.auction.dto.BidDto;
import com.scopic.auction.dto.MoneyDto;
import com.scopic.auction.dto.UserDto;
import com.scopic.auction.utils.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.scopic.auction.utils.Whitebox.getFieldValue;
import static com.scopic.auction.utils.Whitebox.setFieldValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
        assertSame(user, getFieldValue(objectToTest, "bidder"));
        assertSame(time, getFieldValue(objectToTest, "time"));
        assertSame(amount, getFieldValue(objectToTest, "amount"));
    }

    @Test
    void toDtoTest() {
        User user = Mockito.mock(User.class);
        Item item = Mockito.mock(Item.class);
        final LocalDateTime time = LocalDateTime.now();
        final UUID id = UUID.randomUUID();
        Money amount = Mockito.mock(Money.class);
        setFieldValue(objectToTest, "id", id);
        setFieldValue(objectToTest, "bidder", user);
        setFieldValue(objectToTest, "item", item);
        setFieldValue(objectToTest, "time", time);
        setFieldValue(objectToTest, "amount", amount);

        UserDto userDto = Mockito.mock(UserDto.class);
        Mockito.when(user.toDto()).thenReturn(userDto);
        MoneyDto amountDto = Mockito.mock(MoneyDto.class);
        Mockito.when(amount.toDto()).thenReturn(amountDto);

        final BidDto bidDto = objectToTest.toDto();

        assertEquals(id.toString(), bidDto.id);
        assertEquals(userDto, bidDto.user);
        assertEquals(time, bidDto.time);
        assertEquals(amountDto, bidDto.amount);

        Mockito.verify(item, Mockito.never()).toDto();
    }

    @Test
    void isBiggerThanTest() {
        Money amount1 = Mockito.mock(Money.class);
        Money amount2 = Mockito.mock(Money.class);
        Money amount3 = Mockito.mock(Money.class);
        setFieldValue(objectToTest, "amount", amount1);

        Mockito.when(amount1.isBiggerThan(amount2)).thenReturn(true);
        Mockito.when(amount1.isBiggerThan(amount3)).thenReturn(false);

        assertTrue(objectToTest.isBiggerThan(amount2));
        assertFalse(objectToTest.isBiggerThan(amount3));
    }

    @Test
    void isAboutTest() {
        final var correctItem = Mockito.mock(Item.class);
        final var wrongItem = Mockito.mock(Item.class);

        setFieldValue(objectToTest, "item", correctItem);

        assertTrue(objectToTest.isAbout(correctItem));
        assertFalse(objectToTest.isAbout(wrongItem));
    }

    @Test
    void incrementForTest() {
        final var bidder = Mockito.mock(User.class);
        final var item = Mockito.mock(Item.class);
        final var amount = Mockito.mock(Money.class);
        final var nextAmount = Mockito.mock(Money.class);

        Mockito.when(amount.nextAmount()).thenReturn(nextAmount);

        setFieldValue(objectToTest, "item", item);
        setFieldValue(objectToTest, "amount", amount);

        final var newBid = objectToTest.incrementFor(bidder);

        assertEquals(item, getFieldValue(newBid, "item"));
        assertEquals(bidder, getFieldValue(newBid, "bidder"));
        assertEquals(nextAmount, getFieldValue(newBid, "amount"));
        final LocalDateTime time = getFieldValue(newBid, "time");
        assertThat(time, CoreMatchers.notOlderThan(500));
    }

    @Test
    void addWithAmountTest() {
        final var amount = Mockito.mock(Money.class);
        final var amountToAdd = Mockito.mock(Money.class);
        final var expected = Mockito.mock(Money.class);

        setFieldValue(objectToTest, "amount", amount);

        Mockito.when(amount.add(amountToAdd)).thenReturn(expected);

        assertEquals(expected, objectToTest.addWithAmount(amountToAdd));
    }
}