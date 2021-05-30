package com.scopic.auction.repository.jpa;

import com.scopic.auction.domain.Money;
import com.scopic.auction.dto.MoneyDto;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Objects;

@Converter(autoApply = true)
public class MoneyConverter implements AttributeConverter<Money, String> {

    @Override
    public String convertToDatabaseColumn(Money attribute) {
        if (attribute == null) {
            return null;
        }
        MoneyDto data = attribute.toDto(true);
        return data.currency + ";"
                + data.defaultFractionDigits + ";"
                + data.value;
    }

    @Override
    public Money convertToEntityAttribute(String dbData) {
        if (Objects.isNull(dbData) || dbData.length() == 0) {
            return null;
        }
        String[] split = dbData.split(";");
        try {
            return new Money(Long.parseLong(split[2]), split[0], Integer.parseInt(split[1]));
        } catch (Throwable e) {
            throw new IllegalArgumentException(
                    String.format("'%s' is an illegal argument for Money object creation", dbData), e);
        }
    }

}
