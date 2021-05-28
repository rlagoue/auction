package com.scopic.auction.api;

import com.scopic.auction.dto.ItemDto;
import com.scopic.auction.dto.ItemFetchDto;
import com.scopic.auction.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class InventoryResources {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryResources(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/item")
    @ResponseBody
    public ItemFetchDto getItems(@RequestParam("pageIndex") int pageIndex) {
        return inventoryService.getItems(pageIndex);
    }

    @GetMapping("/item/{id}")
    @ResponseBody
    public ItemDto getItemById(@PathVariable("id") String id) {
        return inventoryService.getItemById(id);
    }
}
