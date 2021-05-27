import {Currency} from "./Currency";

export class Money {
    static Null = new Money(0, Currency.USD);
    readonly value: number;
    readonly currency: Currency;

    constructor(value: number, currency: Currency) {
        this.value = value;
        this.currency = currency;
    }

    static fromDataToDomain(data: Money) {
        return new Money(data.value, data.currency);
    }

    toString = (): string => {
        return new Intl.NumberFormat("en", {
            style: "currency",
            currency: this.currency
        }).format(this.value);
    };

    isNull = (): boolean => {
        return this === Money.Null;
    }

    add = (amount: Money): Money => {
        return new Money(this.value + amount.value, this.currency);
    };

    subtract = (amount: Money): Money => {
        return new Money(this.value - amount.value, this.currency);
    };

    multiplyBy = (factor: number): Money => {
        return new Money(this.value * factor, this.currency);
    };
}