package com.scopic.auction.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class AuctionService {

    private final UserService userService;

    @Autowired
    public AuctionService(UserService userService) {
        this.userService = userService;
    }

    public String makeManualBid(String itemId, Number bid, String bidderId) {
        try {
            return this.userService.makeManualBid(itemId, bid, bidderId);
        } catch (ObjectOptimisticLockingFailureException e) {
            return "original-state-changed";
        } catch (Throwable e) {
            return e.getMessage();
        }
    }

}
