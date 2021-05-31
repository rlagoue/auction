package com.scopic.auction.domain;

import com.scopic.auction.dto.BidDto;
import com.scopic.auction.dto.ItemDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static com.scopic.auction.utils.Whitebox.getFieldValue;
import static com.scopic.auction.utils.Whitebox.setFieldValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

        Bid bid1 = mock(Bid.class);
        Bid bid2 = mock(Bid.class);
        setFieldValue(objectToTest, "bids", Set.of(bid1, bid2));

        BidDto bidDto1 = mock(BidDto.class);
        BidDto bidDto2 = mock(BidDto.class);
        when(bid1.toDto()).thenReturn(bidDto1);
        when(bid2.toDto()).thenReturn(bidDto2);

        final ItemDto itemDto = objectToTest.toDto();

        assertEquals(id.toString(), itemDto.id);
        assertEquals(name, itemDto.name);
        assertEquals(description, itemDto.description);
        assertEquals(2, itemDto.bids.size());
        assertTrue(itemDto.bids.contains(bidDto1));
        assertTrue(itemDto.bids.contains(bidDto2));
    }

    @Test
    void makeManualBidWithLowerAmountTest() {
        Bid existingBid1 = mock(Bid.class);
        Bid existingBid2 = mock(Bid.class);

        final var amount = mock(Money.class);
        when(existingBid2.isBiggerThan(amount)).thenReturn(true);
        setFieldValue(objectToTest, "bids", new HashSet<>(Set.of(existingBid1, existingBid2)));

        User leadingBidder = mock(User.class);
        setFieldValue(objectToTest, "leadingBidder", leadingBidder);

        final var newBids = objectToTest.makeManualBid(mock(User.class), amount);

        assertEquals(leadingBidder, getFieldValue(objectToTest, "leadingBidder"));
        assertTrue(newBids.isEmpty());
    }

    @Test
    void makeFirstManualBidTest() {
        User newBidder = mock(User.class);
        Money amount = new Money(10d, "USD");
        Set<Bid> bids = getFieldValue(objectToTest, "bids");
        int size = bids.size();

        final Bid bid = mock(Bid.class);
        when(
                newBidder.makeManualBid(objectToTest, amount, Optional.empty())
        ).thenReturn(bid);

        final Collection<Bid> result = objectToTest.makeManualBid(newBidder, amount);

        assertEquals(1, result.size());
        assertTrue(result.contains(bid));
        assertEquals(newBidder, getFieldValue(objectToTest, "leadingBidder"));

        bids = getFieldValue(objectToTest, "bids");
        assertEquals(size + 1, bids.size());
        assertTrue(bids.contains(bid));
    }

    @Test
    void makeManualBidSuccessfullyTest() {
        Set<Bid> bids = getFieldValue(objectToTest, "bids");
        int size = bids.size();

        final var leadingBidder = mock(User.class);
        setFieldValue(objectToTest, "leadingBidder", leadingBidder);

        User newBidder = mock(User.class);
        Money amount = new Money(10d, "USD");
        final Bid bid = mock(Bid.class);
        when(
                newBidder.makeManualBid(objectToTest, amount, Optional.of(leadingBidder))
        ).thenReturn(bid);
        when(
                leadingBidder.tryToOutbidOn(objectToTest, Optional.of(leadingBidder))
        ).thenReturn(Collections.emptyList());

        final Collection<Bid> result = objectToTest.makeManualBid(newBidder, amount);

        assertEquals(1, result.size());
        assertTrue(result.contains(bid));

        bids = getFieldValue(objectToTest, "bids");
        assertEquals(size + 1, bids.size());
        assertTrue(bids.contains(bid));

        assertEquals(newBidder, getFieldValue(objectToTest, "leadingBidder"));
    }

    @Test
    void makeManualBidWithOutBidByTheAutoBidTest() {
        User newBidder = mock(User.class);
        Money amount = new Money(10d, "USD");
        Set<Bid> bids = getFieldValue(objectToTest, "bids");
        int size = bids.size();

        final var leadingBidder = mock(User.class);
        setFieldValue(objectToTest, "leadingBidder", leadingBidder);
        final Bid bid1 = mock(Bid.class);
        when(
                newBidder.makeManualBid(objectToTest, amount, Optional.of(leadingBidder))
        ).thenReturn(bid1);
        final var bid2 = mock(Bid.class);
        when(
                leadingBidder.tryToOutbidOn(objectToTest, Optional.of(newBidder))
        ).thenReturn(List.of(bid2));

        final Collection<Bid> result = objectToTest.makeManualBid(newBidder, amount);

        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(bid1, bid2)));

        bids = getFieldValue(objectToTest, "bids");
        assertEquals(size + 2, bids.size());
        assertTrue(bids.contains(bid1));
        assertTrue(bids.contains(bid2));

        assertEquals(leadingBidder, getFieldValue(objectToTest, "leadingBidder"));
    }

    @Test
    void isCurrentBidBiggerThanTrueTest() {
        final var bid = mock(Bid.class);
        final var currentBid = mock(Bid.class);
        setFieldValue(objectToTest, "bids", Set.of(bid, currentBid));

        Money amount = mock(Money.class);
        when(currentBid.isBiggerThan(amount)).thenReturn(true);

        try (final var bidMockedStatic = Mockito.mockStatic(Bid.class)) {
            bidMockedStatic.when(() -> Bid.newestOf(Set.of(bid, currentBid))).thenReturn(Optional.of(currentBid));
            assertTrue(objectToTest.isCurrentBidBiggerThan(amount));
        }
    }

    @Test
    void isCurrentBidBiggerThanFalseTest() {
        final var bid = mock(Bid.class);
        final var currentBid = mock(Bid.class);
        setFieldValue(objectToTest, "bids", Set.of(bid, currentBid));

        Money amount = mock(Money.class);
        when(currentBid.isBiggerThan(amount)).thenReturn(false);

        try (final var bidMockedStatic = Mockito.mockStatic(Bid.class)) {
            bidMockedStatic.when(() -> Bid.newestOf(Set.of(bid, currentBid))).thenReturn(Optional.of(currentBid));
            assertFalse(objectToTest.isCurrentBidBiggerThan(amount));
        }
    }

    @Test
    void isCurrentBidBiggerThanWheNoBidPresentTest() {
        Money amount = mock(Money.class);
        assertFalse(objectToTest.isCurrentBidBiggerThan(amount));
    }

    @Test
    void addWithCurrentBidTest() {
        final var amount = mock(Money.class);
        final var expected = mock(Money.class);

        final var bid = mock(Bid.class);
        final var currentBid = mock(Bid.class);
        setFieldValue(objectToTest, "bids", Set.of(bid, currentBid));

        when(currentBid.addWithAmount(amount)).thenReturn(expected);

        try (final var bidMockedStatic = Mockito.mockStatic(Bid.class)) {
            bidMockedStatic.when(() -> Bid.newestOf(Set.of(bid, currentBid))).thenReturn(Optional.of(currentBid));
            assertEquals(expected, objectToTest.addWithCurrentBid(amount));
        }
    }

    @Test
    void addWithCurrentBidWhenNoBidPresentTest() {
        final var amount = mock(Money.class);
        assertEquals(amount, objectToTest.addWithCurrentBid(amount));
    }

    @Test
    void tryAutoBidForWithNewBidTest() {
        final var leadingBidder = mock(User.class);
        setFieldValue(objectToTest, "leadingBidder", leadingBidder);

        Set<Bid> bids = getFieldValue(objectToTest, "bids");
        final var size = bids.size();

        final var newBid = mock(Bid.class);
        List<Bid> anyResponse = List.of(newBid);
        final var newBidder = mock(User.class);
        when(newBidder.tryToOutbidOn(objectToTest, Optional.of(leadingBidder))).thenReturn(anyResponse);

        final var potentialNewBid = objectToTest.tryAutoBidFor(newBidder);

        assertEquals(newBidder, getFieldValue(objectToTest, "leadingBidder"));
        assertEquals(anyResponse, potentialNewBid);
        bids = getFieldValue(objectToTest, "bids");
        assertEquals(size+1, bids.size());
        assertTrue(bids.contains(newBid));
    }

    @Test
    void tryAutoBidForWithoutBidCreatedTest() {
        final var leadingBidder = mock(User.class);
        setFieldValue(objectToTest, "leadingBidder", leadingBidder);

        Set<Bid> bids = getFieldValue(objectToTest, "bids");
        final var size = bids.size();

        final var newBidder = mock(User.class);
        when(newBidder.tryToOutbidOn(objectToTest, Optional.of(leadingBidder))).thenReturn(Collections.emptyList());

        final var potentialNewBid = objectToTest.tryAutoBidFor(newBidder);

        assertEquals(leadingBidder, getFieldValue(objectToTest, "leadingBidder"));
        assertTrue(potentialNewBid.isEmpty());
        bids = getFieldValue(objectToTest, "bids");
        assertEquals(size, bids.size());
    }
}