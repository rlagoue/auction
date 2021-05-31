package com.scopic.auction.domain;

import com.scopic.auction.dto.MoneyDto;
import com.scopic.auction.dto.SettingsDto;
import com.scopic.auction.dto.UserDto;
import com.scopic.auction.utils.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.scopic.auction.utils.Whitebox.getFieldValue;
import static com.scopic.auction.utils.Whitebox.setFieldValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User objectToTest;

    @BeforeEach
    void setUp() {
        objectToTest = new User();
    }

    @Test
    void userTest() {
        final String username = "username";
        objectToTest = new User(username);

        assertEquals(username, getFieldValue(objectToTest, "username"));
    }

    @Test
    void toDtoTest() {
        final String username = "user1";
        setFieldValue(objectToTest, "username", username);

        final UserDto userDto = objectToTest.toDto();

        assertEquals(username, userDto.username);
    }

    @Test
    void userWithUsernameAndAmountTest() {
        Money amount = Mockito.mock(Money.class);
        final String username = "username";
        objectToTest = new User(username, amount);

        assertSame(amount, getFieldValue(objectToTest, "maxBidAmount"));
        assertSame(username, getFieldValue(objectToTest, "username"));
    }

    @Test
    void getSettingsTest() {
        final String username = "username";
        MoneyDto amountDto = Mockito.mock(MoneyDto.class);

        final Money amount = Mockito.mock(Money.class);
        Mockito.when(amount.toDto()).thenReturn(amountDto);
        setFieldValue(objectToTest, "username", username);
        setFieldValue(objectToTest, "maxBidAmount", amount);

        final SettingsDto dto = objectToTest.getSettings();

        assertEquals(amountDto, dto.maxBidAmount);
    }

    @Test
    void updateTest() throws InvalidNewMaxBidAmountException {
        final Money oldAmount = Mockito.mock(Money.class);
        setFieldValue(objectToTest, "maxBidAmount", oldAmount);

        final var item1 = Mockito.mock(Item.class);
        final var item2 = Mockito.mock(Item.class);
        final var item3 = Mockito.mock(Item.class);
        setFieldValue(objectToTest, "autoBidItems", Set.of(item1, item2, item3));

        final var bid1 = Mockito.mock(Bid.class);
        final var bid2 = Mockito.mock(Bid.class);
        setFieldValue(objectToTest, "leadingBids", Set.of(bid1, bid2));

        Mockito.when(bid1.isAbout(item1)).thenReturn(true);

        final Money newAmount = Mockito.mock(Money.class);
        Money engagedAmount = Mockito.mock(Money.class);
        Mockito.when(engagedAmount.isBiggerThan(newAmount)).thenReturn(false);

        try (final var bidMockedStatic = Mockito.mockStatic(Bid.class)) {
            bidMockedStatic.when(() -> Bid.sum(Set.of(bid1))).thenReturn(engagedAmount);
            objectToTest.update(newAmount);
        }

        assertEquals(newAmount, getFieldValue(objectToTest, "maxBidAmount"));
        Mockito.verify(item2).tryAutoBidFor(objectToTest);
        Mockito.verify(item3).tryAutoBidFor(objectToTest);
    }

    @Test
    void updateWithMaxAmountSmallerThanEngagedBidSumTest() {
        final Money oldAmount = Mockito.mock(Money.class);
        setFieldValue(objectToTest, "maxBidAmount", oldAmount);

        final var bid1 = Mockito.mock(Bid.class);
        final var bid2 = Mockito.mock(Bid.class);
        final var bid3 = Mockito.mock(Bid.class);
        setFieldValue(objectToTest, "leadingBids", Set.of(bid1, bid2, bid3));

        final var item1 = Mockito.mock(Item.class);
        final var item2 = Mockito.mock(Item.class);
        setFieldValue(objectToTest, "autoBidItems", Set.of(item1, item2));

        Mockito.when(bid1.isAbout(item1)).thenReturn(true);
        Mockito.when(bid2.isAbout(item2)).thenReturn(true);

        final Money newAmount = Mockito.mock(Money.class);
        final Money engagedAmount = Mockito.mock(Money.class);
        Mockito.when(engagedAmount.isBiggerThan(newAmount)).thenReturn(true);

        try (final var bidMockedStatic = Mockito.mockStatic(Bid.class)) {
            bidMockedStatic.when(() -> Bid.sum(Set.of(bid1, bid2))).thenReturn(engagedAmount);

            final var exception = assertThrows(
                    InvalidNewMaxBidAmountException.class,
                    () -> objectToTest.update(newAmount)
            );
            assertEquals(
                    "newMaxBidAmountSmallerThanCurrentEngagement",
                    exception.getMessage()
            );
        }


    }

    @Test
    void activateAutoBidTest() {
        final Item item = Mockito.mock(Item.class);
        Set<Item> autoBidItems = getFieldValue(objectToTest, "autoBidItems");
        int size = autoBidItems.size();

        final Bid bid = Mockito.mock(Bid.class);
        Mockito.when(bid.isAbout(item)).thenReturn(true);
        setFieldValue(objectToTest, "leadingBids", Set.of(bid));

        objectToTest.activateAutoBidOn(item);

        autoBidItems = getFieldValue(objectToTest, "autoBidItems");
        assertEquals(size + 1, autoBidItems.size());
        assertTrue(autoBidItems.contains(item));

        Set<Bid> leadingBids = getFieldValue(objectToTest, "leadingBids");
        assertEquals(1, leadingBids.size());

        Mockito.verify(item).tryAutoBidFor(objectToTest);
    }

    @Test
    void tryAutoBidOnItemWhenCashAvailableByTheNewBidderIsSmallerThanCurrentBid() {
        final var item = Mockito.mock(Item.class);
        final var item1 = Mockito.mock(Item.class);

        var currentLeadingBidder = new User("currentLeadingBidder");

        var bid1 = Mockito.mock(Bid.class);
        var bid2 = Mockito.mock(Bid.class);
        final var newBidderLeadingBids = Set.of(bid1, bid2);
        setFieldValue(objectToTest, "leadingBids", newBidderLeadingBids);

        Mockito.when(bid1.isAbout(item1)).thenReturn(true);
        Mockito.when(bid2.isAbout(item1)).thenReturn(true);

        final var newBidderMaxBidAmount = Mockito.mock(Money.class);
        setFieldValue(objectToTest, "maxBidAmount", newBidderMaxBidAmount);
        setFieldValue(objectToTest, "autoBidItems", Set.of(item, item1));

        final Money newBidderLeadingBidsSum = Mockito.mock(Money.class);
        final Money newBidderAvailableCash = Mockito.mock(Money.class);
        Mockito.when(newBidderMaxBidAmount.subtract(newBidderLeadingBidsSum))
                .thenReturn(newBidderAvailableCash);

        Mockito.when(item.isCurrentBidBiggerThan(newBidderAvailableCash)).thenReturn(true);

        try (final var bidMockedStatic = Mockito.mockStatic(Bid.class)) {
            bidMockedStatic.when(() -> Bid.sum(newBidderLeadingBids))
                    .thenReturn(newBidderLeadingBidsSum);

            final var outbid = objectToTest.tryToOutbidOn(item, Optional.of(currentLeadingBidder));
            assertTrue(outbid.isEmpty());
        }
    }

    @Test
    void tryAutoBidOnItemNotActivatedByByNewBidderForAutoBidTest() {
        var item = Mockito.mock(Item.class);

        var currentLeadingBidder = new User("currentLeadingBidder");
        setFieldValue(currentLeadingBidder, "autoBidItems", Set.of(item));

        final var outbid = objectToTest.tryToOutbidOn(item, Optional.of(currentLeadingBidder));
        assertTrue(outbid.isEmpty());
    }

    @Test
    void tryAutoBidOnItemWhenNewBidderHasMoreCashThanCurrentBidderTest() {
        final var item1 = Mockito.mock(Item.class);
        final var item = Mockito.mock(Item.class);

        final var currentLeadingBidderMaxBidAmount = Mockito.mock(Money.class);
        var currentLeadingBidder = new User("currentLeadingBidder", currentLeadingBidderMaxBidAmount);
        setFieldValue(currentLeadingBidder, "autoBidItems", Set.of(item));
        setFieldValue(currentLeadingBidder, "autoBidItems", Set.of(item, item1));

        var bid1 = Mockito.mock(Bid.class);
        var bid2 = Mockito.mock(Bid.class);
        Mockito.when(bid1.isAbout(item)).thenReturn(false);
        Mockito.when(bid2.isAbout(item)).thenReturn(true);
        Mockito.when(bid1.isAbout(item1)).thenReturn(true);
        final var currentLeadingBidderLeadingBids = new HashSet<>(Set.of(bid1, bid2));
        setFieldValue(currentLeadingBidder, "leadingBids", currentLeadingBidderLeadingBids);

        final var currentLeadingBidderLeadingBidsSum = Mockito.mock(Money.class);
        final var currentLeadingBidderAvailableCash = Mockito.mock(Money.class);
        final var currentLeadingBidderAvailableCashIfNoBidOnItem = Mockito.mock(Money.class);
        Mockito.when(currentLeadingBidderMaxBidAmount.subtract(currentLeadingBidderLeadingBidsSum))
                .thenReturn(currentLeadingBidderAvailableCashIfNoBidOnItem);
        Mockito.when(item.addWithCurrentBid(currentLeadingBidderAvailableCashIfNoBidOnItem))
                .thenReturn(currentLeadingBidderAvailableCash);

        setFieldValue(objectToTest, "username", "user1");
        var bid3 = Mockito.mock(Bid.class);
        var bid4 = Mockito.mock(Bid.class);
        Mockito.when(bid3.isAbout(item)).thenReturn(false);
        Mockito.when(bid4.isAbout(item)).thenReturn(false);

        Mockito.when(bid3.isAbout(item1)).thenReturn(true);
        Mockito.when(bid4.isAbout(item1)).thenReturn(true);

        final var newLeadingBidderLeadingBids = new HashSet<>(Set.of(bid3, bid4));
        setFieldValue(objectToTest, "leadingBids", newLeadingBidderLeadingBids);

        final var newBidderMaxBidAmount = Mockito.mock(Money.class);
        setFieldValue(objectToTest, "maxBidAmount", newBidderMaxBidAmount);
        setFieldValue(objectToTest, "autoBidItems", Set.of(item, item1));

        final var newBidderLeadingBidsSum = Mockito.mock(Money.class);
        final var newBidderAvailableCash = Mockito.mock(Money.class);
        Mockito.when(newBidderMaxBidAmount.subtract(newBidderLeadingBidsSum)).thenReturn(newBidderAvailableCash);

        Mockito.when(item.isCurrentBidBiggerThan(newBidderAvailableCash)).thenReturn(false);
        Mockito.when(newBidderAvailableCash.isBiggerThan(currentLeadingBidderAvailableCash)).thenReturn(true);
        final var nextBidAmount = Mockito.mock(Money.class);
        Mockito.when(currentLeadingBidderAvailableCash.nextAmount()).thenReturn(nextBidAmount);

        try (final var bidMockedStatic = Mockito.mockStatic(Bid.class)) {
            bidMockedStatic.when(() -> Bid.sum(currentLeadingBidderLeadingBids))
                    .thenReturn(currentLeadingBidderLeadingBidsSum);

            bidMockedStatic.when(() -> Bid.sum(newLeadingBidderLeadingBids))
                    .thenReturn(newBidderLeadingBidsSum);

            final var outbid = objectToTest.tryToOutbidOn(item, Optional.of(currentLeadingBidder));
            assertFalse(outbid.isEmpty());
            final var bid = outbid.get();
            assertEquals(item, getFieldValue(bid, "item"));
            assertEquals(objectToTest, getFieldValue(bid, "bidder"));
            assertEquals(nextBidAmount, getFieldValue(bid, "amount"));
            final LocalDateTime time = getFieldValue(bid, "time");
            assertThat(time, CoreMatchers.notOlderThan(500));

            Set<Bid> leadingBids = getFieldValue(objectToTest, "leadingBids");
            assertEquals(3, leadingBids.size());
            assertTrue(leadingBids.contains(bid));
            assertTrue(leadingBids.containsAll(Set.of(bid3, bid4)));

            leadingBids = getFieldValue(currentLeadingBidder, "leadingBids");
            assertEquals(1, leadingBids.size());
            assertTrue(leadingBids.contains(bid1));
        }
    }

    @Test
    void tryAutoBidOnItemWhenCurrentBidderHasMoreCashThanNewBidderTest() {
        final var item = Mockito.mock(Item.class);
        final var item1 = Mockito.mock(Item.class);

        final var currentLeadingBidderMaxBidAmount = Mockito.mock(Money.class);
        var currentLeadingBidder = new User("currentLeadingBidder", currentLeadingBidderMaxBidAmount);
        setFieldValue(currentLeadingBidder, "autoBidItems", Set.of(item));

        var bid1 = Mockito.mock(Bid.class);
        var bid2 = Mockito.mock(Bid.class);
        Mockito.when(bid1.isAbout(item)).thenReturn(false);
        Mockito.when(bid2.isAbout(item)).thenReturn(true);
        final var currentLeadingBidderLeadingBids = new HashSet<>(Set.of(bid1, bid2));
        setFieldValue(currentLeadingBidder, "leadingBids", currentLeadingBidderLeadingBids);

        final var currentLeadingBidderLeadingBidsSum = Mockito.mock(Money.class);
        final var currentLeadingBidderAvailableCash = Mockito.mock(Money.class);
        final var currentLeadingBidderAvailableCashIfNoBidOnItem = Mockito.mock(Money.class);
        Mockito.when(currentLeadingBidderMaxBidAmount.subtract(currentLeadingBidderLeadingBidsSum))
                .thenReturn(currentLeadingBidderAvailableCashIfNoBidOnItem);
        Mockito.when(item.addWithCurrentBid(currentLeadingBidderAvailableCashIfNoBidOnItem))
                .thenReturn(currentLeadingBidderAvailableCash);

        setFieldValue(objectToTest, "username", "user1");
        var bid3 = Mockito.mock(Bid.class);
        var bid4 = Mockito.mock(Bid.class);
        Mockito.when(bid3.isAbout(item)).thenReturn(false);
        Mockito.when(bid4.isAbout(item)).thenReturn(false);
        Mockito.when(bid3.isAbout(item1)).thenReturn(true);
        Mockito.when(bid4.isAbout(item1)).thenReturn(true);
        final var newLeadingBidderLeadingBids = new HashSet<>(Set.of(bid3, bid4));
        setFieldValue(objectToTest, "leadingBids", newLeadingBidderLeadingBids);

        final var newBidderMaxBidAmount = Mockito.mock(Money.class);
        setFieldValue(objectToTest, "maxBidAmount", newBidderMaxBidAmount);
        setFieldValue(objectToTest, "autoBidItems", Set.of(item, item1));

        final var newBidderLeadingBidsSum = Mockito.mock(Money.class);
        final var newBidderAvailableCash = Mockito.mock(Money.class);
        Mockito.when(newBidderMaxBidAmount.subtract(newBidderLeadingBidsSum))
                .thenReturn(newBidderAvailableCash);

        Mockito.when(item.isCurrentBidBiggerThan(newBidderAvailableCash)).thenReturn(false);
        Mockito.when(newBidderAvailableCash.isBiggerThan(currentLeadingBidderAvailableCash)).thenReturn(false);
        final var nextBidAmount = Mockito.mock(Money.class);
        Mockito.when(newBidderAvailableCash.nextAmount()).thenReturn(nextBidAmount);

        try (final var bidMockedStatic = Mockito.mockStatic(Bid.class)) {
            bidMockedStatic.when(() -> Bid.sum(Set.of(bid4)))
                    .thenReturn(currentLeadingBidderLeadingBidsSum);

            bidMockedStatic.when(() -> Bid.sum(newLeadingBidderLeadingBids))
                    .thenReturn(newBidderLeadingBidsSum);

            final var outbid = objectToTest.tryToOutbidOn(item, Optional.of(currentLeadingBidder));
            assertFalse(outbid.isEmpty());
            final var bid = outbid.get();
            assertEquals(item, getFieldValue(bid, "item"));
            assertEquals(currentLeadingBidder, getFieldValue(bid, "bidder"));
            assertEquals(nextBidAmount, getFieldValue(bid, "amount"));
            final LocalDateTime time = getFieldValue(bid, "time");
            assertThat(time, CoreMatchers.notOlderThan(500));

            Set<Bid> leadingBids = getFieldValue(objectToTest, "leadingBids");
            assertEquals(2, leadingBids.size());
            assertTrue(leadingBids.containsAll(Set.of(bid3, bid4)));

            leadingBids = getFieldValue(currentLeadingBidder, "leadingBids");
            assertEquals(3, leadingBids.size());
            assertTrue(leadingBids.contains(bid));
            assertTrue(leadingBids.containsAll(Set.of(bid1, bid2)));
        }
    }

    @Test
    void tryAutoBidOnItemWhenCurrentBidderHasMoreCashThanNewBidderButHasNotActivatedAutoBidOnItemTest() {
        var item = Mockito.mock(Item.class);

        final var currentLeadingBidderMaxBidAmount = Mockito.mock(Money.class);
        var currentLeadingBidder = new User("currentLeadingBidder", currentLeadingBidderMaxBidAmount);

        var bid1 = Mockito.mock(Bid.class);
        var bid2 = Mockito.mock(Bid.class);
        Mockito.when(bid1.isAbout(item)).thenReturn(false);
        Mockito.when(bid2.isAbout(item)).thenReturn(true);
        final var currentLeadingBidderLeadingBids = new HashSet<>(Set.of(bid1, bid2));
        setFieldValue(currentLeadingBidder, "leadingBids", currentLeadingBidderLeadingBids);

        final var currentLeadingBidderLeadingBidsSum = Mockito.mock(Money.class);
        final var currentLeadingBidderAvailableCash = Mockito.mock(Money.class);
        final var currentLeadingBidderAvailableCashIfNoBidOnItem = Mockito.mock(Money.class);
        Mockito.when(currentLeadingBidderMaxBidAmount.subtract(currentLeadingBidderLeadingBidsSum))
                .thenReturn(currentLeadingBidderAvailableCashIfNoBidOnItem);
        Mockito.when(item.addWithCurrentBid(currentLeadingBidderAvailableCashIfNoBidOnItem))
                .thenReturn(currentLeadingBidderAvailableCash);

        setFieldValue(objectToTest, "username", "user1");
        var bid3 = Mockito.mock(Bid.class);
        var bid4 = Mockito.mock(Bid.class);
        Mockito.when(bid3.isAbout(item)).thenReturn(false);
        Mockito.when(bid4.isAbout(item)).thenReturn(false);
        final var newLeadingBidderLeadingBids = new HashSet<>(Set.of(bid3, bid4));
        setFieldValue(objectToTest, "leadingBids", newLeadingBidderLeadingBids);

        final var newBidderMaxBidAmount = Mockito.mock(Money.class);
        setFieldValue(objectToTest, "maxBidAmount", newBidderMaxBidAmount);
        setFieldValue(objectToTest, "autoBidItems", Set.of(item));

        final var newBidderLeadingBidsSum = Mockito.mock(Money.class);
        final var newBidderAvailableCash = Mockito.mock(Money.class);
        Mockito.when(newBidderMaxBidAmount.subtract(newBidderLeadingBidsSum))
                .thenReturn(newBidderAvailableCash);

        Mockito.when(item.isCurrentBidBiggerThan(newBidderAvailableCash)).thenReturn(false);
        Mockito.when(newBidderAvailableCash.isBiggerThan(currentLeadingBidderAvailableCash)).thenReturn(false);

        final var newBid = Mockito.mock(Bid.class);
        Mockito.when(bid2.incrementFor(objectToTest)).thenReturn(newBid);

        try (final var bidMockedStatic = Mockito.mockStatic(Bid.class)) {
            bidMockedStatic.when(() -> Bid.sum(currentLeadingBidderLeadingBids))
                    .thenReturn(currentLeadingBidderLeadingBidsSum);

            bidMockedStatic.when(() -> Bid.sum(newLeadingBidderLeadingBids))
                    .thenReturn(newBidderLeadingBidsSum);

            bidMockedStatic.when(() -> Bid.newestOf(currentLeadingBidderLeadingBids))
                    .thenReturn(Optional.of(bid2));

            final var outbid = objectToTest.tryToOutbidOn(item, Optional.of(currentLeadingBidder));
            assertFalse(outbid.isEmpty());
            final var bid = outbid.get();
            assertSame(newBid, bid);

            Set<Bid> leadingBids = getFieldValue(objectToTest, "leadingBids");
            assertEquals(3, leadingBids.size());
            assertTrue(leadingBids.contains(bid));
            assertTrue(leadingBids.containsAll(Set.of(bid3, bid4)));

            leadingBids = getFieldValue(currentLeadingBidder, "leadingBids");
            assertEquals(1, leadingBids.size());
            assertTrue(leadingBids.contains(bid1));
        }

    }

    @Test
    void deactivateAutoBidTest() {
        final var item1 = Mockito.mock(Item.class);
        final var item2 = Mockito.mock(Item.class);
        Set<Item> autoBidItems = getFieldValue(objectToTest, "autoBidItems");
        autoBidItems.add(item1);
        autoBidItems.add(item2);
        int size = autoBidItems.size();

        final Bid bid = Mockito.mock(Bid.class);
        setFieldValue(objectToTest, "leadingBids", Set.of(bid));

        objectToTest.deactivateAutoBidOn(item2);

        autoBidItems = getFieldValue(objectToTest, "autoBidItems");
        assertEquals(size - 1, autoBidItems.size());
        assertTrue(autoBidItems.contains(item1));
    }

    @Test
    void deactivateAutoBidOnItemWhenLeadingTest() {
        final var item1 = Mockito.mock(Item.class);
        final var item2 = Mockito.mock(Item.class);
        Set<Item> autoBidItems = getFieldValue(objectToTest, "autoBidItems");
        autoBidItems.add(item1);
        autoBidItems.add(item2);
        int size = autoBidItems.size();

        final Bid bid = Mockito.mock(Bid.class);
        setFieldValue(objectToTest, "leadingBids", Set.of(bid));

        Mockito.when(bid.isAbout(item2)).thenReturn(true);


        final var exception = assertThrows(
                IllegalStateException.class,
                () -> objectToTest.deactivateAutoBidOn(item2)
        );

        assertEquals("CannotDeactivateAutoBidOnItemWhenLeading", exception.getMessage());

        autoBidItems = getFieldValue(objectToTest, "autoBidItems");
        assertEquals(size, autoBidItems.size());
        assertTrue(autoBidItems.contains(item1));
        assertTrue(autoBidItems.contains(item2));
    }

}