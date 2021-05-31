package com.scopic.auction.service;

import com.scopic.auction.domain.*;
import com.scopic.auction.dto.MoneyDto;
import com.scopic.auction.dto.SettingsDto;
import com.scopic.auction.repository.BidRepository;
import com.scopic.auction.repository.ItemRepository;
import com.scopic.auction.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService objectToTest;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BidRepository bidRepository;

    @BeforeEach
    void setUp() {
        objectToTest = new UserService(userRepository, itemRepository, bidRepository);
    }

    @Test
    void getSettingsTest() {
        final String username = "username";

        final var spiedObjectToTest = Mockito.spy(objectToTest);

        User user = mock(User.class);
        doReturn(user).when(spiedObjectToTest).getById(username);

        SettingsDto expected = mock(SettingsDto.class);
        when(user.getSettings()).thenReturn(expected);

        final SettingsDto settingsDto = spiedObjectToTest.getSettings(username);

        assertSame(expected, settingsDto);
    }

    @Test
    void updateSettingsTest() throws InvalidNewMaxBidAmountException {
        final String username = "username";
        SettingsDto data = new SettingsDto();
        final double value = 10d;
        data.maxBidAmount = new MoneyDto();
        data.maxBidAmount.value = value;
        data.maxBidAmount.currency = "USD";
        User user = mock(User.class);

        final UserService spiedObjectToTest = Mockito.spy(objectToTest);
        doReturn(user).when(spiedObjectToTest).getById(username);

        final var bid1 = mock(Bid.class);
        final var bid2 = mock(Bid.class);
        when(user.update(new Money(value, "USD"))).thenReturn(List.of(bid1, bid2));

        spiedObjectToTest.updateSettings(username, data);

        verify(bidRepository).saveAll(List.of(bid1, bid2));
        verify(userRepository).saveAndFlush(user);
    }

    @Test
    void getByIdForTheFirstTimeTest() {
        final String username = "username";
        SettingsDto data = new SettingsDto();
        final double value = 10d;
        data.maxBidAmount = new MoneyDto();
        data.maxBidAmount.value = value;
        data.maxBidAmount.currency = "USD";
        when(userRepository.findById(username))
                .thenReturn(Optional.empty());
        when(userRepository.existsById(username)).thenReturn(false);

        final User user = objectToTest.getById(username);

        assertNotNull(user);

        verify(userRepository).saveAndFlush(user);
    }

    @Test
    void makeManualBidSuccessfullyTest() {
        final String username = "user1";
        final long newBidValue = 100L;
        final UUID itemId = UUID.randomUUID();
        final String itemIdAsString = itemId.toString();

        Item item = mock(Item.class);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        final var bid = mock(Bid.class);
        when(
                item.makeManualBid(any(User.class), any(Money.class))
        ).thenReturn(List.of(bid));

        final String result = objectToTest.makeManualBid(itemIdAsString, newBidValue, username);
        assertEquals("success", result);
        verify(item).makeManualBid(
                new User(username),
                new Money(newBidValue, "USD")
        );
        verify(bidRepository).saveAll(List.of(bid));
        verify(itemRepository).save(item);
    }

    @Test
    void makeManualBidWithSmallerAmountTest() {
        final String username = "user1";
        final long newBidValue = 100L;
        final UUID itemId = UUID.randomUUID();
        final String itemIdAsString = itemId.toString();

        Item item = mock(Item.class);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(
                item.makeManualBid(any(User.class), any(Money.class))
        ).thenReturn(List.of());

        final String result = objectToTest.makeManualBid(itemIdAsString, newBidValue, username);
        assertEquals("outbidded", result);
        verify(item).makeManualBid(
                new User(username),
                new Money(newBidValue, "USD")
        );
        verify(bidRepository, never()).saveAll(any());
        verify(itemRepository).save(item);
    }

    @Test
    void makeManualBidChallengingBiggerBidderTest() {
        final String username = "user1";
        final long newBidValue = 100L;
        final UUID itemId = UUID.randomUUID();
        final String itemIdAsString = itemId.toString();

        Item item = mock(Item.class);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        final var bid1 = mock(Bid.class);
        final var bid2 = mock(Bid.class);
        when(
                item.makeManualBid(any(User.class), any(Money.class))
        ).thenReturn(List.of(bid1, bid2));

        final String result = objectToTest.makeManualBid(itemIdAsString, newBidValue, username);
        assertEquals("outbidded", result);
        verify(item).makeManualBid(
                new User(username),
                new Money(newBidValue, "USD")
        );
        verify(bidRepository).saveAll(List.of(bid1, bid2));
        verify(itemRepository).save(item);
    }

    @Test
    void activateAutoBidOnItemTest() {
        final UUID itemId = UUID.randomUUID();
        final String itemIdAsString = itemId.toString();
        final String username = "username";
        final Item item = mock(Item.class);

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        final UserService spiedObjectToTest = Mockito.spy(objectToTest);
        User user = mock(User.class);
        doReturn(user).when(spiedObjectToTest).getById(username);
        final var bid1 = mock(Bid.class);
        final var bid2 = mock(Bid.class);
        when(user.activateAutoBidOn(item)).thenReturn(List.of(bid1, bid2));

        spiedObjectToTest.activateAutoBidOnItem(username, itemIdAsString);

        verify(bidRepository).saveAll(List.of(bid1, bid2));
        verify(itemRepository).save(item);
        verify(userRepository).save(user);
    }

    @Test
    void deactivateAutoBidOnItemTest() {
        final UUID itemId = UUID.randomUUID();
        final String itemIdAsString = itemId.toString();
        final String username = "username";
        final Item item = mock(Item.class);
        User user = mock(User.class);

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        final UserService spiedObjectToTest = Mockito.spy(objectToTest);
        doReturn(user).when(spiedObjectToTest).getById(username);

        spiedObjectToTest.deactivateAutoBidOnItem(username, itemIdAsString);

        verify(user).deactivateAutoBidOn(item);
        verify(userRepository).saveAndFlush(user);
    }
}