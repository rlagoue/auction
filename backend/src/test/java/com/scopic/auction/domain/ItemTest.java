package com.scopic.auction.domain;

import com.scopic.auction.dto.BidDto;
import com.scopic.auction.dto.ItemDto;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.scopic.auction.utils.Whitebox.getFieldValue;
import static com.scopic.auction.utils.Whitebox.setFieldValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    private Item objectToTest;

    @BeforeEach
    void setUp() {
        objectToTest = new Item();
    }

    @Test
    void toDtoTest() {
        final UUID id = UUID.randomUUID();
        final String name = "name";
        final String description = "description";

        objectToTest = new Item(name, description);
        setFieldValue(objectToTest, "id", id);

        Bid bid1 = Mockito.mock(Bid.class);
        Bid bid2 = Mockito.mock(Bid.class);
        setFieldValue(objectToTest, "bids", List.of(bid1, bid2));

        BidDto bidDto1 = Mockito.mock(BidDto.class);
        BidDto bidDto2 = Mockito.mock(BidDto.class);
        Mockito.when(bid1.toDto()).thenReturn(bidDto1);
        Mockito.when(bid2.toDto()).thenReturn(bidDto2);

        final ItemDto itemDto = objectToTest.toDto();

        assertEquals(id.toString(), itemDto.id);
        assertEquals(name, itemDto.name);
        assertEquals(description, itemDto.description);
        assertEquals(2, itemDto.bids.size());
        assertTrue(itemDto.bids.contains(bidDto1));
        assertTrue(itemDto.bids.contains(bidDto2));
    }

    @Test
    void makeABidSuccessfullyTest() {
        User user = Mockito.mock(User.class);
        Money amount = new Money(10d, "USD");
        List<Bid> bids = getFieldValue(objectToTest, "bids");
        int size = bids.size();

        final Bid bid = objectToTest.makeABid(user, amount);

        assertSame(objectToTest, getFieldValue(bid, "item"));
        assertSame(amount, getFieldValue(bid, "amount"));
        assertEquals(user, getFieldValue(bid, "user"));
        assertThat(
                getFieldValue(bid, "time"),
                notOlderThan(500)
        );

        bids = getFieldValue(objectToTest, "bids");

        assertEquals(size + 1, bids.size());
        assertTrue(bids.contains(bid));
    }

    private Matcher<LocalDateTime> notOlderThan(long milliseconds) {
        return new BaseMatcher<LocalDateTime>() {
            @Override
            public boolean matches(Object actual) {
                return Duration.between(
                        LocalDateTime.now(),
                        (LocalDateTime) actual
                ).abs().toMillis() <= milliseconds;
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }

    @Test
    void makeABidFailureTest() {
        User user = Mockito.mock(User.class);
        Money amount = new Money(10d, "USD");

        Bid bid1 = Mockito.mock(Bid.class);
        Bid bid2 = Mockito.mock(Bid.class);

        List<Bid> bids = getFieldValue(objectToTest, "bids");
        bids.add(bid1);
        bids.add(bid2);
        int size = bids.size();

        Mockito.when(bid1.isBiggerThan(amount)).thenReturn(true);
        Mockito.when(bid2.isBiggerThan(amount)).thenReturn(false);

        final Bid bid = objectToTest.makeABid(user, amount);

        assertNull(bid);
        bids = getFieldValue(objectToTest, "bids");
        assertEquals(size, bids.size());
    }
}