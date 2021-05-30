package com.scopic.auction.service;

import com.scopic.auction.domain.InvalidNewMaxBidAmountException;
import com.scopic.auction.domain.Item;
import com.scopic.auction.domain.Money;
import com.scopic.auction.domain.User;
import com.scopic.auction.dto.MoneyDto;
import com.scopic.auction.dto.SettingsDto;
import com.scopic.auction.repository.ItemRepository;
import com.scopic.auction.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService objectToTest;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        objectToTest = new UserService(userRepository, itemRepository);
    }

    @Test
    void getSettingsTest() {
        final String username = "username";

        User user = Mockito.mock(User.class);
        Mockito.when(userRepository.findById(username))
                .thenReturn(Optional.of(user));
        SettingsDto expected = Mockito.mock(SettingsDto.class);
        Mockito.when(user.getSettings()).thenReturn(expected);

        final SettingsDto settingsDto = objectToTest.getSettings(username);

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
        User user = Mockito.mock(User.class);

        final UserService spiedObjectToTest = Mockito.spy(objectToTest);
        Mockito.doReturn(user).when(spiedObjectToTest).getById(username);

        spiedObjectToTest.updateSettings(username, data);

        Mockito.verify(user).update(new Money(value, "USD"));
        Mockito.verify(userRepository).saveAndFlush(user);
    }

    @Test
    void getByIdForTheFirstTimeTest() {
        final String username = "username";
        SettingsDto data = new SettingsDto();
        final double value = 10d;
        data.maxBidAmount = new MoneyDto();
        data.maxBidAmount.value = value;
        data.maxBidAmount.currency = "USD";
        Mockito.when(userRepository.findById(username))
                .thenReturn(Optional.empty());
        Mockito.when(userRepository.existsById(username)).thenReturn(false);

        final User user = objectToTest.getById(username);

        assertNotNull(user);

        Mockito.verify(userRepository).saveAndFlush(user);
    }

    @Test
    void activateAutoBidOnItemTest() {
        final UUID itemId = UUID.randomUUID();
        final String itemIdAsString = itemId.toString();
        final String username = "username";
        final Item item = Mockito.mock(Item.class);
        User user = Mockito.mock(User.class);

        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        final UserService spiedObjectToTest = Mockito.spy(objectToTest);
        Mockito.doReturn(user).when(spiedObjectToTest).getById(username);

        spiedObjectToTest.activateAutoBidOnItem(username, itemIdAsString);

        Mockito.verify(user).activateAutoBid(item);
        Mockito.verify(userRepository).saveAndFlush(user);
    }

    @Test
    void makeABidSuccessfullyTest() {
        final String username = "user1";
        final long newBidValue = 100L;
        final UUID itemId = UUID.randomUUID();
        final String itemIdAsString = itemId.toString();


        Item item = Mockito.mock(Item.class);
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        final String success = "success";
        Mockito.when(
                item.makeABid(Mockito.any(User.class), Mockito.any(Money.class))
        ).thenReturn(success);

        final String result = objectToTest.makeABid(itemIdAsString, newBidValue, username);

        assertEquals(success, result);
        Mockito.verify(item).makeABid(
                new User(username),
                new Money(newBidValue, "USD")
        );
        Mockito.verify(itemRepository).saveAndFlush(item);
    }
}