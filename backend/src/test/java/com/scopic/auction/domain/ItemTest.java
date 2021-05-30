package com.scopic.auction.domain;

import com.scopic.auction.dto.BidDto;
import com.scopic.auction.dto.ItemDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.scopic.auction.utils.Whitebox.getFieldValue;
import static com.scopic.auction.utils.Whitebox.setFieldValue;
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
        setFieldValue(objectToTest, "bids", Set.of(bid1, bid2));

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
    void makeFirstBidTest() {
        User user = Mockito.mock(User.class);
        Money amount = new Money(10d, "USD");
        Set<Bid> bids = getFieldValue(objectToTest, "bids");
        int size = bids.size();

        final Bid bid = Mockito.mock(Bid.class);
        Mockito.when(
                user.makeABid(objectToTest, amount, Optional.empty())
        ).thenReturn(bid);

        final String result = objectToTest.makeABid(user, amount);

        assertEquals("success", result);
        assertEquals(user, getFieldValue(objectToTest, "currentBidder"));

        bids = getFieldValue(objectToTest, "bids");
        assertEquals(size + 1, bids.size());
        assertTrue(bids.contains(bid));
    }

    @Test
    void makeABidSuccessfullyTest() {
        User user = Mockito.mock(User.class);
        Money amount = new Money(10d, "USD");
        Set<Bid> bids = getFieldValue(objectToTest, "bids");
        int size = bids.size();

        final var currentBidder = Mockito.mock(User.class);
        setFieldValue(objectToTest, "currentBidder", currentBidder);
        final Bid bid = Mockito.mock(Bid.class);
        Mockito.when(
                user.makeABid(objectToTest, amount, Optional.of(currentBidder))
        ).thenReturn(bid);
        Mockito.when(
                currentBidder.tryToOutbidOn(objectToTest, currentBidder)
        ).thenReturn(Optional.empty());

        final String result = objectToTest.makeABid(user, amount);

        assertEquals("success", result);

        bids = getFieldValue(objectToTest, "bids");

        assertEquals(size + 1, bids.size());
        assertTrue(bids.contains(bid));
    }

    @Test
    void makeABidFailureTest() {
        User user = Mockito.mock(User.class);
        Money amount = new Money(10d, "USD");
        Set<Bid> bids = getFieldValue(objectToTest, "bids");
        int size = bids.size();

        final var currentBidder = Mockito.mock(User.class);
        setFieldValue(objectToTest, "currentBidder", currentBidder);
        final Bid bid1 = Mockito.mock(Bid.class);
        Mockito.when(
                user.makeABid(objectToTest, amount, Optional.of(currentBidder))
        ).thenReturn(bid1);
        final var bid2 = Mockito.mock(Bid.class);
        Mockito.when(
                currentBidder.tryToOutbidOn(objectToTest, user)
        ).thenReturn(Optional.of(bid2));

        final String result = objectToTest.makeABid(user, amount);

        assertEquals("outbidded", result);

        bids = getFieldValue(objectToTest, "bids");

        assertEquals(size + 2, bids.size());
        assertTrue(bids.contains(bid1));
        assertTrue(bids.contains(bid2));
    }

    @Test
    void isCurrentBidBiggerThanTrueTest() {
        final var bid = Mockito.mock(Bid.class);
        final var currentBid = Mockito.mock(Bid.class);
        setFieldValue(objectToTest, "bids", Set.of(bid, currentBid));

        Money amount = Mockito.mock(Money.class);
        Mockito.when(currentBid.isBiggerThan(amount)).thenReturn(true);

        try (final var bidMockedStatic = Mockito.mockStatic(Bid.class)) {
            bidMockedStatic.when(() -> Bid.newestOf(Set.of(bid, currentBid))).thenReturn(Optional.of(currentBid));
            assertTrue(objectToTest.isCurrentBidBiggerThan(amount));
        }
    }

    @Test
    void isCurrentBidBiggerThanFalseTest() {
        final var bid = Mockito.mock(Bid.class);
        final var currentBid = Mockito.mock(Bid.class);
        setFieldValue(objectToTest, "bids", Set.of(bid, currentBid));

        Money amount = Mockito.mock(Money.class);
        Mockito.when(currentBid.isBiggerThan(amount)).thenReturn(false);

        try (final var bidMockedStatic = Mockito.mockStatic(Bid.class)) {
            bidMockedStatic.when(() -> Bid.newestOf(Set.of(bid, currentBid))).thenReturn(Optional.of(currentBid));
            assertFalse(objectToTest.isCurrentBidBiggerThan(amount));
        }
    }

    @Test
    void isCurrentBidBiggerThanWheNoBidPresentTest() {
        Money amount = Mockito.mock(Money.class);
        assertFalse(objectToTest.isCurrentBidBiggerThan(amount));
    }

    @Test
    void addWithCurrentBidTest() {
        final var amount = Mockito.mock(Money.class);
        final var expected = Mockito.mock(Money.class);

        final var bid = Mockito.mock(Bid.class);
        final var currentBid = Mockito.mock(Bid.class);
        setFieldValue(objectToTest, "bids", Set.of(bid, currentBid));

        Mockito.when(currentBid.addWithAmount(amount)).thenReturn(expected);

        try (final var bidMockedStatic = Mockito.mockStatic(Bid.class)) {
            bidMockedStatic.when(() -> Bid.newestOf(Set.of(bid, currentBid))).thenReturn(Optional.of(currentBid));
            assertEquals(expected, objectToTest.addWithCurrentBid(amount));
        }
    }

    @Test
    void addWithCurrentBidWhenNoBidPresentTest() {
        final var amount = Mockito.mock(Money.class);
        assertEquals(amount, objectToTest.addWithCurrentBid(amount));
    }

    @Test
    void makeNextPossibleBidTest() {
        final var newBidder = Mockito.mock(User.class);
        final var bid = Mockito.mock(Bid.class);
        final var lastBid = Mockito.mock(Bid.class);

        objectToTest = Mockito.spy(Item.class);

        final Bid newBid = Mockito.mock(Bid.class);
        Mockito.when(lastBid.incrementFor(newBidder)).thenReturn(newBid);

        Mockito.doReturn("anyResponse").when(objectToTest).registerNewBid(newBid, newBidder);

        try (final var bidMockedStatic = Mockito.mockStatic(Bid.class)) {
            bidMockedStatic.when(() -> Bid.newestOf(Set.of(bid, lastBid)))
                    .thenReturn(Optional.of(lastBid));
            objectToTest.makeNextPossibleBid(newBidder);
        }
    }
}