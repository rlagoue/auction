package com.scopic.auction.dto;

public class MoneyDto {
    public long value;
    public String currency;
    public int defaultFractionDigits;

    public MoneyDto() {
    }

    public MoneyDto(long value, String currency, int defaultFractionDigits) {
        this.currency = currency;
        this.defaultFractionDigits = defaultFractionDigits;
        this.value = value;
    }
}
