package com.scopic.auction.domain;

import com.scopic.auction.dto.SettingsDto;
import com.scopic.auction.repository.jpa.MoneyConverter;

import javax.persistence.*;

@Entity
@Table(name = "t_settings")
public class Settings {
    @Id
    @Column(name = "c_username")
    private String username;
    @Basic
    @Column(name = "c_maxbidamount")
    @Convert(converter = MoneyConverter.class)
    private Money maxBidAmount;

    public Settings() {
    }

    public Settings(String username, Money maxBidAmount) {
        this.username = username;
        this.maxBidAmount = maxBidAmount;
    }

    public SettingsDto toDto() {
        final SettingsDto result = new SettingsDto();
        result.maxBidAmount = this.maxBidAmount.toDto();
        return result;
    }

    public void update(Money maxBidAmount) {

    }
}
