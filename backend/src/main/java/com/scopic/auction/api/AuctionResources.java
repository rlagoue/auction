package com.scopic.auction.api;

import com.scopic.auction.domain.InvalidNewMaxBidAmountException;
import com.scopic.auction.dto.SettingsDto;
import com.scopic.auction.service.AuctionService;
import com.scopic.auction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuctionResources extends BaseResources {

    private final AuctionService auctionService;
    private final UserService userService;

    @Autowired
    public AuctionResources(AuctionService auctionService, UserService userService) {
        this.auctionService = auctionService;
        this.userService = userService;
    }

    @PostMapping("/item/{itemId}/bid/{bid}")
    @ResponseBody
    public String makeABid(
            @PathVariable("itemId") String itemId,
            @PathVariable("bid") Number bid
    ) {
        return auctionService.makeABid(itemId, bid, getCurrentUsername());
    }

    @GetMapping("/user/{username}/settings")
    @ResponseBody
    public SettingsDto getSettings(@PathVariable("username") String username) {
        return userService.getSettings(username);
    }

    @PutMapping("/user/{username}/settings")
    @ResponseBody
    public String updateSettings(
            @PathVariable("username") String username,
            @RequestBody SettingsDto data
    ) {
        try {
            userService.updateSettings(username, data);
            return "success";
        } catch (InvalidNewMaxBidAmountException e) {
            return e.getMessage();
        }
    }

    @PostMapping("/activate-auto-bid/{itemId}")
    public void activateAutoBidOnItem(@PathVariable("itemId") String itemId) {
        userService.activateAutoBidOnItem(getCurrentUsername(), itemId);
    }

    @PostMapping("/activate-auto-bid/{itemId}")
    public void deactivateAutoBidOnItem(@PathVariable("itemId") String itemId) {
        userService.deactivateAutoBidOnItem(getCurrentUsername(), itemId);
    }
}
