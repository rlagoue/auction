package com.scopic.auction.repository.jpa;

import com.scopic.auction.domain.Money;
import com.scopic.auction.dto.MoneyDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Currency;

import static com.scopic.auction.utils.Whitebox.getFieldValue;
import static org.junit.jupiter.api.Assertions.*;

class MoneyConverterTest {
    public MoneyConverter objectToTest = new MoneyConverter();

    @Test
    public void convertToDatabaseColumnTest() {
        Money money = new Money(20L, "XAF", 0);

        String databaseColumnValue = objectToTest.convertToDatabaseColumn(money);

        assertEquals("XAF;0;20", databaseColumnValue);
    }

    @Test
    public void convertToDatabaseColumnNullArgumentTest() {
        assertNull(objectToTest.convertToDatabaseColumn(null));
    }

    @Test
    public void convertToEntityAttributeTest() {
        Money entityAttribute = objectToTest.convertToEntityAttribute("XAF;0;20");

        assertEquals(Currency.getInstance("XAF"), getFieldValue(entityAttribute, "currency"));
        assertEquals(0, (int) getFieldValue(entityAttribute, "defaultFractionDigits"));
        assertEquals(20L, (long) getFieldValue(entityAttribute, "value"));
    }

    @Test
    public void convertToEntityAttributeNullTest() {
        assertNull(objectToTest.convertToEntityAttribute(null));
    }

    @Test
    public void convertToEntityAttributeEmptyTest() {
        assertNull(objectToTest.convertToEntityAttribute(""));
    }

    @Test
    public void convertToEntityAttributeMalformedTest() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> objectToTest.convertToEntityAttribute("XAF;;4"));
        assertEquals("'XAF;;4' is an illegal argument for Money object creation", exception.getMessage());
    }

}