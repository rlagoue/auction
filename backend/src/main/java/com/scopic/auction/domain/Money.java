package com.scopic.auction.domain;


import com.scopic.auction.dto.MoneyDto;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

public class Money {
    private final Currency currency;
    private final int defaultFractionDigits;
    private long value;

    public Money(Long value, Currency currency, Integer defaultFractionDigits) {
        this.value = value == null ? 0l : value;
        this.currency = Objects.requireNonNull(currency, "Currency cannot be null");
        if (defaultFractionDigits == null) {
            this.defaultFractionDigits = computeDefaultFractionDigits();
        } else {
            this.defaultFractionDigits = defaultFractionDigits;
        }
    }

    public Money(Long value, String currencyCode, Integer defaultFractionDigits) {
        this(value, Currency.getInstance(currencyCode.toUpperCase()), defaultFractionDigits);
    }

    public Money(Number value, Currency currency) {
        this(null, currency, null);
        if (value != null) {
            double d = value.doubleValue() * Math.pow(10, defaultFractionDigits);
            this.value = Math.round(d);
        }
    }

    public Money(Number value, String currencyCode) {
        this(value, Currency.getInstance(currencyCode));
    }

    private int computeDefaultFractionDigits() {
        String currencyCode = this.currency.getCurrencyCode();
        switch (currencyCode) {
            case "XAF":
                return 0;
            case "NGN":
            case "CNY":
            case "RUB":
            case "USD":
            case "INR":
            case "EUR":
            case "GBP":
            case "ZAR":
                return 2;
            default:
                throw new IllegalArgumentException("Currency with the code '" + currencyCode + "' not supported.");
        }
    }

    private Number computeOriginalValue() {
        if (defaultFractionDigits == 0) {
            return value;
        }
        return value / Math.pow(10, defaultFractionDigits);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((currency == null) ? 0 : currency.hashCode());
        result = prime * result + defaultFractionDigits;
        result = prime * result + (int) (value ^ (value >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Money other = (Money) obj;
        if (currency == null) {
            if (other.currency != null)
                return false;
        } else if (!currency.equals(other.currency))
            return false;
        if (defaultFractionDigits != other.defaultFractionDigits)
            return false;
        return value == other.value;
    }

    public Money add(Money operand) {
        assertPreconditions(operand);
        Money result = new Money(0d, this.currency);
        result.value = this.value + operand.value;
        return result;
    }

    protected void assertPreconditions(Money operand) {
        Objects.requireNonNull(operand, "Null argument not allowed");
        if (!this.currency.equals(operand.currency)) {
            throw new IllegalArgumentException(String.format(
                    "Arithmetic operations are not allowed on money with different currency. !! %s - %s !!",
                    this.currency.getCurrencyCode(), operand.currency.getCurrencyCode()));
        }
    }

    public Money subtract(Money operand) {
        assertPreconditions(operand);
        Money result = new Money(0d, this.currency);
        result.value = this.value - operand.value;
        return result;
    }

    public Money multiplyBy(double operand) {
        return new Money(
                BigDecimal.valueOf(this.computeOriginalValue().doubleValue()).multiply(BigDecimal.valueOf(operand)),
                currency);
    }

    public MoneyDto toDto() {
        return new MoneyDto(
                computeOriginalValue().longValue(),
                this.currency.getCurrencyCode(),
                defaultFractionDigits
        );
    }

    public boolean isBiggerThan(Money amount) {
        return this.value >= amount.value;
    }
}
