package com.scopic.auction.dto;

public class MoneyDto {
    public Number value;
    public String currency;
    public int defaultFractionDigits;

    public MoneyDto() {
    }

    public MoneyDto(Number value, String currency, int defaultFractionDigits) {
        this.currency = currency;
        this.defaultFractionDigits = defaultFractionDigits;
        this.value = value;
    }
}
