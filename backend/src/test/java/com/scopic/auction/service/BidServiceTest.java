package com.scopic.auction.service;

import com.scopic.auction.domain.Bid;
import com.scopic.auction.domain.Item;
import com.scopic.auction.domain.Money;
import com.scopic.auction.domain.User;
import com.scopic.auction.dto.MakeBidDto;
import com.scopic.auction.dto.UserDto;
import com.scopic.auction.repository.BidRepository;
import com.scopic.auction.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class BidServiceTest {

    private BidService objectToTest;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BidRepository bidRepository;

    @BeforeEach
    void setUp() {
        objectToTest = new BidService(itemRepository, bidRepository);
    }

    @Test
    void makeABidSuccessfullyTest() {
        final String username = "user1";
        MakeBidDto data = new MakeBidDto();
        data.user = new UserDto(username);
        final long newBidValue = 100L;
        data.bid = newBidValue;
        final UUID itemId = UUID.randomUUID();
        final String itemIdAsString = itemId.toString();


        final Bid bid = Mockito.mock(Bid.class);
        Item item = Mockito.mock(Item.class);
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        Mockito.when(
                item.makeABid(Mockito.any(User.class), Mockito.any(Money.class))
        ).thenReturn(bid);

        final String result = objectToTest.makeABid(itemIdAsString, data);

        assertEquals("success", result);
        Mockito.verify(item).makeABid(
                new User(username),
                new Money(newBidValue, "USD")
        );
        Mockito.verify(itemRepository).save(item);
        Mockito.verify(bidRepository).saveAndFlush(bid);
    }
}