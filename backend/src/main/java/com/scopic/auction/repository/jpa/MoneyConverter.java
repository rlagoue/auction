package com.scopic.auction.repository.jpa;

import com.scopic.auction.domain.Money;
import com.scopic.auction.dto.MoneyDto;
import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class MoneyConverter implements AttributeConverter<Money, String> {

    @Override
    public String convertToDatabaseColumn(Money attribute) {
        if (attribute == null) {
            return null;
        }
        MoneyDto data = attribute.toDto();
        try {
            return data.currency + ";"
                    + data.defaultFractionDigits + ";"
                    + data.value;
        } catch (Throwable e) {
            throw new IllegalArgumentException("Money object passed is not well formatted to be serialized to String",
                    e);
        }
    }

    @Override
    public Money convertToEntityAttribute(String dbData) {
        if (StringUtils.isEmpty(dbData)) {
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
