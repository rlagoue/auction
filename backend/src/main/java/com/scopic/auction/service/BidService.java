package com.scopic.auction.service;

import com.scopic.auction.domain.Bid;
import com.scopic.auction.domain.Money;
import com.scopic.auction.domain.User;
import com.scopic.auction.dto.MakeBidDto;
import com.scopic.auction.repository.BidRepository;
import com.scopic.auction.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class BidService {
    private final ItemRepository itemRepository;
    private final BidRepository bidRepository;

    public BidService(ItemRepository itemRepository, BidRepository bidRepository) {
        this.itemRepository = itemRepository;
        this.bidRepository = bidRepository;
    }

    @Transactional
    public String makeABid(String itemId, MakeBidDto data) {
        return itemRepository.findById(UUID.fromString(itemId)).map(item -> {
            User user = new User(data.user.username);
            Money amount = new Money(data.bid, "USD");
            final Bid bid = item.makeABid(user, amount);
            itemRepository.save(item);
            bidRepository.saveAndFlush(bid);
            return "success";
        }).orElse("outbidded");
    }

}
