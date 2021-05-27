package com.scopic.auction.domain;

import com.scopic.auction.dto.MoneyDto;
import org.junit.jupiter.api.Test;

import java.util.Currency;

import static com.scopic.auction.utils.Whitebox.getFieldValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoneyTest {
    Money objectToTest;

    @Test
    public void moneyValueCurrencyFractionDigitsTest() {
        Long expectedValue = 500l;
        String currencyAsString = "XAF";
        Integer expectedDefaultFractionDigits = 0;

        objectToTest = new Money(expectedValue, currencyAsString, expectedDefaultFractionDigits);

        assertEquals(expectedValue, getFieldValue(objectToTest, "value"));
        assertEquals(Currency.getInstance(currencyAsString), getFieldValue(objectToTest, "currency"));
        assertEquals(expectedDefaultFractionDigits, getFieldValue(objectToTest, "defaultFractionDigits"));

        expectedValue = 50020l;
        Currency currency = Currency.getInstance("NGN");
        expectedDefaultFractionDigits = 2;

        objectToTest = new Money(expectedValue, currency, expectedDefaultFractionDigits);
        assertEquals(expectedValue, getFieldValue(objectToTest, "value"));
        assertEquals(currency, getFieldValue(objectToTest, "currency"));
        assertEquals(expectedDefaultFractionDigits, getFieldValue(objectToTest, "defaultFractionDigits"));
    }

    @Test
    public void moneyValueCurrencyNotInUpperCaseFractionDigitsTest() {
        moneyValueCurrencyNotInUpperCaseFractionDigitsTest("xaf");
        moneyValueCurrencyNotInUpperCaseFractionDigitsTest("Xaf");
        moneyValueCurrencyNotInUpperCaseFractionDigitsTest("xaF");
    }

    protected void moneyValueCurrencyNotInUpperCaseFractionDigitsTest(String currencyCode) {
        Long expectedValue = 500l;
        Integer expectedDefaultFractionDigits = 0;

        objectToTest = new Money(expectedValue, currencyCode, expectedDefaultFractionDigits);

        assertEquals(expectedValue, getFieldValue(objectToTest, "value"));
        assertEquals(Currency.getInstance("XAF"), getFieldValue(objectToTest, "currency"));
        assertEquals(expectedDefaultFractionDigits, getFieldValue(objectToTest, "defaultFractionDigits"));
    }

    @Test
    public void moneyValueCurrencyTest() {
        moneyValueCurrencyTest("XAF", 4l, 4l, Integer.valueOf(0));
        moneyValueCurrencyTest("XAF", 4.2d, 4l, Integer.valueOf(0));
        moneyValueCurrencyTest("XAF", 4.5d, 5l, Integer.valueOf(0));
        moneyValueCurrencyTest("NGN", 4l, 400l, Integer.valueOf(2));
        moneyValueCurrencyTest("NGN", 4.05, 405l, Integer.valueOf(2));
        moneyValueCurrencyTest("CNY", 4l, 400l, Integer.valueOf(2));
        moneyValueCurrencyTest("CNY", 4.05, 405l, Integer.valueOf(2));
        moneyValueCurrencyTest("RUB", 4l, 400l, Integer.valueOf(2));
        moneyValueCurrencyTest("RUB", 4.05, 405l, Integer.valueOf(2));
        moneyValueCurrencyTest("INR", 4l, 400l, Integer.valueOf(2));
        moneyValueCurrencyTest("INR", 4.05, 405l, Integer.valueOf(2));
        moneyValueCurrencyTest("GBP", 4l, 400l, Integer.valueOf(2));
        moneyValueCurrencyTest("GBP", 4.05, 405l, Integer.valueOf(2));
        moneyValueCurrencyTest("EUR", 4l, 400l, Integer.valueOf(2));
        moneyValueCurrencyTest("EUR", 4.05, 405l, Integer.valueOf(2));
        moneyValueCurrencyTest("USD", 4l, 400l, Integer.valueOf(2));
        moneyValueCurrencyTest("USD", 4.05, 405l, Integer.valueOf(2));
        moneyValueCurrencyTest("ZAR", 4l, 400l, Integer.valueOf(2));
        moneyValueCurrencyTest("ZAR", 4.05, 405l, Integer.valueOf(2));
    }

    private void moneyValueCurrencyTest(String currencyCode, Number value, Long expectedStoreValue,
                                        Integer expectedDefaultFractionDigits) {
        Currency currency = Currency.getInstance(currencyCode);
        objectToTest = new Money(value, currency);

        assertEquals(expectedStoreValue, getFieldValue(objectToTest, "value"));
        assertEquals(currency, getFieldValue(objectToTest, "currency"));
        assertEquals(expectedDefaultFractionDigits, getFieldValue(objectToTest, "defaultFractionDigits"));
    }

    @Test
    public void moneyValueCurrencyCodeTest() {
        Long expectedValue = 4l;
        String currencyCode = "XAF";
        objectToTest = new Money(expectedValue, currencyCode);

        assertEquals(expectedValue, getFieldValue(objectToTest, "value"));
        assertEquals(Currency.getInstance(currencyCode), getFieldValue(objectToTest, "currency"));
        assertEquals(Integer.valueOf(0), getFieldValue(objectToTest, "defaultFractionDigits"));
    }

    @Test
    public void moneyNullValueTest() {
        objectToTest = new Money(null, "XAF");
    }

    @Test
    public void moneyNullCurrencyTest() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            new Money(4l, (Currency) null);
        });
        assertEquals("Currency cannot be null", exception.getMessage());
    }

    @Test
    public void moneyDecimalValueCurrencyCodeTest() {
        moneyDecimalValueCurrencyCodeTest("XAF", 0, 4.21d, 4l);
        moneyDecimalValueCurrencyCodeTest("XAF", 0, 4.51d, 5l);
        moneyDecimalValueCurrencyCodeTest("CNY", 2, 4.21d, 421l);
        moneyDecimalValueCurrencyCodeTest("CNY", 2, 4.212d, 421l);
        moneyDecimalValueCurrencyCodeTest("CNY", 2, 4.215d, 422l);
    }

    private void moneyDecimalValueCurrencyCodeTest(String currencyCode, Integer expectedDefaultFractionDigits,
                                                   Double decimalValue, Long expectedValue) {
        objectToTest = new Money(decimalValue, currencyCode);

        assertEquals(expectedValue, getFieldValue(objectToTest, "value"));
        assertEquals(Currency.getInstance(currencyCode), getFieldValue(objectToTest, "currency"));
        assertEquals(expectedDefaultFractionDigits, getFieldValue(objectToTest, "defaultFractionDigits"));
    }


    @Test
    public void addTest() {
        objectToTest = new Money(10d, "XAF");

        Money operand = new Money(20d, "XAF");
        Money result = objectToTest.add(operand);

        assertEquals(Long.valueOf(30), getFieldValue(result, "value"));
        assertEquals(Currency.getInstance("XAF"), getFieldValue(result, "currency"));
        assertEquals(Long.valueOf(10), getFieldValue(objectToTest, "value"));
        assertEquals(Currency.getInstance("XAF"), getFieldValue(objectToTest, "currency"));
        assertEquals(Long.valueOf(20), getFieldValue(operand, "value"));
        assertEquals(Currency.getInstance("XAF"), getFieldValue(operand, "currency"));
    }

    @Test
    public void addWithDecimalPartTest() {
        objectToTest = new Money(10d, "NGN");

        Money operand = new Money(20d, "NGN");
        Money result = objectToTest.add(operand);

        assertEquals(Long.valueOf(3000), getFieldValue(result, "value"));
        assertEquals(Currency.getInstance("NGN"), getFieldValue(result, "currency"));
        assertEquals(Long.valueOf(1000), getFieldValue(objectToTest, "value"));
        assertEquals(Currency.getInstance("NGN"), getFieldValue(objectToTest, "currency"));
        assertEquals(Long.valueOf(2000), getFieldValue(operand, "value"));
        assertEquals(Currency.getInstance("NGN"), getFieldValue(operand, "currency"));
    }

    @Test
    public void addDifferentCurrencyTest() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Money(10d, "XAF").add(new Money(20d, "NGN"));
        });
        assertEquals("Arithmetic operations are not allowed on money with different currency. !! XAF - NGN !!",
                exception.getMessage());

    }

    @Test
    public void addOperandIsNullTest() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            new Money(10d, "XAF").add(null);
        });
        assertEquals("Null argument not allowed", exception.getMessage());
    }

    @Test
    public void substractTest() {
        objectToTest = new Money(10d, "XAF");

        Money operand = new Money(20d, "XAF");
        Money result = objectToTest.subtract(operand);

        assertEquals(Long.valueOf(-10), getFieldValue(result, "value"));
        assertEquals(Currency.getInstance("XAF"), getFieldValue(result, "currency"));
        assertEquals(Long.valueOf(10), getFieldValue(objectToTest, "value"));
        assertEquals(Currency.getInstance("XAF"), getFieldValue(objectToTest, "currency"));
        assertEquals(Long.valueOf(20), getFieldValue(operand, "value"));
        assertEquals(Currency.getInstance("XAF"), getFieldValue(operand, "currency"));
    }

    @Test
    public void substractWithDecimalsTest() {
        objectToTest = new Money(10d, "NGN");

        Money operand = new Money(20d, "NGN");
        Money result = objectToTest.subtract(operand);

        assertEquals(Long.valueOf(-1000), getFieldValue(result, "value"));
        assertEquals(Currency.getInstance("NGN"), getFieldValue(result, "currency"));
        assertEquals(Long.valueOf(1000), getFieldValue(objectToTest, "value"));
        assertEquals(Currency.getInstance("NGN"), getFieldValue(objectToTest, "currency"));
        assertEquals(Long.valueOf(2000), getFieldValue(operand, "value"));
        assertEquals(Currency.getInstance("NGN"), getFieldValue(operand, "currency"));
    }

    @Test
    public void substractDifferentCurrencyTest() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Money(10d, "XAF").subtract(new Money(20d, "NGN"));
        });
        assertEquals("Arithmetic operations are not allowed on money with different currency. !! XAF - NGN !!",
                exception.getMessage());
    }

    @Test
    public void substractOperandIsNullTest() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            new Money(10d, "XAF").subtract(null);
        });
        assertEquals("Null argument not allowed", exception.getMessage());
    }

    @Test
    public void multiplyByTest() {
        objectToTest = new Money(89, "XAF");

        Money result = objectToTest.multiplyBy(82d);

        assertEquals(new Money(82 * 89d, "XAF"), result);
    }

    @Test
    void toDtoTest() {
        final long expectedValue = 2000L;
        final String expectedCurrency = "USD";
        final int expectedFractionDigits = 2;

        objectToTest = new Money(expectedValue, expectedCurrency);

        final MoneyDto moneyDto = objectToTest.toDto();

        assertEquals(expectedValue, moneyDto.value);
        assertEquals(expectedCurrency, moneyDto.currency);
        assertEquals(expectedFractionDigits, moneyDto.defaultFractionDigits);
    }


}