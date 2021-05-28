package com.scopic.auction.service;

import com.scopic.auction.dto.MakeBidDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class AuctionService {

    private final BidService bidService;

    @Autowired
    public AuctionService(BidService bidService) {
        this.bidService = bidService;
    }

    public String makeABid(String itemId, MakeBidDto data) {
        try {
            return this.bidService.makeABid(itemId, data);
        } catch (ObjectOptimisticLockingFailureException e) {
            return "original-state-changed";
        }
    }
}
