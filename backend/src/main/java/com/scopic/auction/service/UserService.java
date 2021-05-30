package com.scopic.auction.service;

import com.scopic.auction.domain.InvalidNewMaxBidAmountException;
import com.scopic.auction.domain.Item;
import com.scopic.auction.domain.Money;
import com.scopic.auction.domain.User;
import com.scopic.auction.dto.SettingsDto;
import com.scopic.auction.repository.ItemRepository;
import com.scopic.auction.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public UserService(UserRepository userRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    User getById(String id) {
        final User user = userRepository.findById(id)
                .orElse(new User(id));
        if (!userRepository.existsById(id)) {
            userRepository.saveAndFlush(user);
        }
        return user;
    }

    @Transactional
    public void updateSettings(String username, SettingsDto data) throws InvalidNewMaxBidAmountException {
        final Money maxBidAmount = new Money(
                data.maxBidAmount.value,
                data.maxBidAmount.currency
        );
        final User user = getById(username);
        user.update(maxBidAmount);
        userRepository.saveAndFlush(user);
    }

    @Transactional
    public SettingsDto getSettings(String username) {
        return this.userRepository.findById(username)
                .map(User::getSettings)
                .orElseThrow();
    }

    @Transactional
    public void activateAutoBidOnItem(String username, String itemId) {
        final User user = getById(username);
        final Item item = itemRepository.findById(UUID.fromString(itemId))
                .orElseThrow();
        user.activateAutoBidOn(item);
        userRepository.saveAndFlush(user);
    }

    @Transactional
    public void deactivateAutoBidOnItem(String username, String itemId) {
        final User user = getById(username);
        final Item item = itemRepository.findById(UUID.fromString(itemId))
                .orElseThrow();
        user.deactivateAutoBidOn(item);
        userRepository.saveAndFlush(user);
    }

    @Transactional
    public String makeABid(String itemId, Number bid, String bidderId) {
        final Item item = itemRepository.findById(UUID.fromString(itemId)).orElseThrow();
        User user = getById(bidderId);
        Money amount = new Money(bid, "USD");
        final var result = item.makeABid(user, amount);
        itemRepository.saveAndFlush(item);
        return result;
    }
}
