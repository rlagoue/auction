package com.scopic.auction.api;

import com.scopic.auction.dto.MakeBidDto;
import com.scopic.auction.dto.SettingsDto;
import com.scopic.auction.service.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuctionResources {

    private final AuctionService auctionService;

    @Autowired
    public AuctionResources(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @PostMapping("/item/{itemId}/bid")
    @ResponseBody
    public String makeABid(
            @PathVariable("itemID") String itemId,
            @RequestBody MakeBidDto data
    ) {
        return auctionService.makeABid(itemId, data);
    }

    @GetMapping("/user/{username}/settings")
    @ResponseBody
    public SettingsDto getSettings(@PathVariable("username") String username) {
        return auctionService.getSettings(username);
    }

    @PutMapping("/user/{username}/settings")
    public void updateSettings(
            @PathVariable("username") String username,
            @RequestBody SettingsDto data
    ) {
        auctionService.updateSettings(username, data);
    }
}
