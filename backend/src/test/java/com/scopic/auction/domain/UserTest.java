package com.scopic.auction.domain;

import com.scopic.auction.dto.MoneyDto;
import com.scopic.auction.dto.SettingsDto;
import com.scopic.auction.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static com.scopic.auction.utils.Whitebox.getFieldValue;
import static com.scopic.auction.utils.Whitebox.setFieldValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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
        Money amount = mock(Money.class);
        final String username = "username";
        objectToTest = new User(username, amount);

        assertSame(amount, getFieldValue(objectToTest, "maxBidAmount"));
        assertSame(username, getFieldValue(objectToTest, "username"));
    }

    @Test
    void getSettingsTest() {
        final String username = "username";
        MoneyDto amountDto = mock(MoneyDto.class);

        final Money amount = mock(Money.class);
        when(amount.toDto()).thenReturn(amountDto);
        setFieldValue(objectToTest, "username", username);
        setFieldValue(objectToTest, "maxBidAmount", amount);

        final SettingsDto dto = objectToTest.getSettings();

        assertEquals(amountDto, dto.maxBidAmount);
    }

    @Test
    void activateAutoBidTest() {
        final Item item = mock(Item.class);
        Set<Item> autoBidItems = getFieldValue(objectToTest, "autoBidItems");
        int size = autoBidItems.size();

        final Bid bid = mock(Bid.class);
        when(item.tryAutoBidFor(objectToTest)).thenReturn(Set.of(bid));

        final var result = objectToTest.activateAutoBidOn(item);

        assertEquals(Set.of(bid), result);

        autoBidItems = getFieldValue(objectToTest, "autoBidItems");
        assertEquals(size + 1, autoBidItems.size());
        assertTrue(autoBidItems.contains(item));
    }

    @Test
    void activateAutoBidOnItemWhenBeingLeadingBidderTest() {
        Set<Item> autoBidItems = getFieldValue(objectToTest, "autoBidItems");
        int size = autoBidItems.size();

        final Item item = mock(Item.class);
        final Bid bid = mock(Bid.class);
        when(bid.isAbout(item)).thenReturn(true);
        setFieldValue(objectToTest, "leadingBids", Set.of(bid));

        final var exception = assertThrows(
                UnsupportedOperationException.class,
                () -> objectToTest.activateAutoBidOn(item)
        );

        assertEquals("CannotActivateAutoBidWhenBeingLeadingBidder", exception.getMessage());

        autoBidItems = getFieldValue(objectToTest, "autoBidItems");
        assertEquals(size, autoBidItems.size());
        assertFalse(autoBidItems.contains(item));
    }


    @Test
    void tryAutoBidOnItemNotActivatedByNewBidderForAutoBidTest() {
        var item = mock(Item.class);

        var currentLeadingBidder = new User("currentLeadingBidder");
        setFieldValue(currentLeadingBidder, "autoBidItems", Set.of(item));

        final var outbid = objectToTest.tryToOutbidOn(item, Optional.of(currentLeadingBidder));
        assertTrue(outbid.isEmpty());
    }

    @Test
    void deactivateAutoBidTest() {
        final var item1 = mock(Item.class);
        final var item2 = mock(Item.class);
        Set<Item> autoBidItems = getFieldValue(objectToTest, "autoBidItems");
        autoBidItems.add(item1);
        autoBidItems.add(item2);
        int size = autoBidItems.size();

        final Bid bid = mock(Bid.class);
        setFieldValue(objectToTest, "leadingBids", Set.of(bid));

        objectToTest.deactivateAutoBidOn(item2);

        autoBidItems = getFieldValue(objectToTest, "autoBidItems");
        assertEquals(size - 1, autoBidItems.size());
        assertTrue(autoBidItems.contains(item1));
    }

    @Test
    void deactivateAutoBidOnItemWhenLeadingTest() {
        final var item1 = mock(Item.class);
        final var item2 = mock(Item.class);
        Set<Item> autoBidItems = getFieldValue(objectToTest, "autoBidItems");
        autoBidItems.add(item1);
        autoBidItems.add(item2);
        int size = autoBidItems.size();

        final Bid bid = mock(Bid.class);
        setFieldValue(objectToTest, "leadingBids", Set.of(bid));

        when(bid.isAbout(item2)).thenReturn(true);


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