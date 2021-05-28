package com.scopic.auction.service;

import com.scopic.auction.domain.Money;
import com.scopic.auction.domain.Settings;
import com.scopic.auction.dto.MakeBidDto;
import com.scopic.auction.dto.SettingsDto;
import com.scopic.auction.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuctionService {

    private final BidService bidService;
    private final SettingsRepository settingsRepository;

    @Autowired
    public AuctionService(
            BidService bidService,
            SettingsRepository settingsRepository
    ) {
        this.bidService = bidService;
        this.settingsRepository = settingsRepository;
    }

    public String makeABid(String itemId, MakeBidDto data) {
        try {
            return this.bidService.makeABid(itemId, data);
        } catch (ObjectOptimisticLockingFailureException e) {
            return "original-state-changed";
        }
    }

    @Transactional
    public SettingsDto getSettings(String username) {
        return this.settingsRepository.findById(username)
                .map(settings -> settings.toDto())
                .orElseThrow();
    }

    @Transactional
    public void updateSettings(String username, SettingsDto data) {
        final Money maxBidAmount = new Money(
                data.maxBidAmount.value,
                data.maxBidAmount.currency
        );
        final Settings settings = settingsRepository.findById(username)
                .orElse(new Settings(username, maxBidAmount));
        settings.update(maxBidAmount);
        settingsRepository.saveAndFlush(settings);
    }
}
