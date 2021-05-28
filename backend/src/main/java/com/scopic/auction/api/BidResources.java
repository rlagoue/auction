package com.scopic.auction.api;

import com.scopic.auction.dto.MakeBidDto;
import com.scopic.auction.service.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BidResources {

    private final BidService bidService;

    @Autowired
    public BidResources(BidService bidService) {
        this.bidService = bidService;
    }

    @PostMapping("/item/{itemId}/bid")
    @ResponseBody
    public String makeABid(
            @PathVariable("itemID") String itemId,
            @RequestBody MakeBidDto data
    ) {
        return bidService.makeABid(itemId, data);
    }
}
