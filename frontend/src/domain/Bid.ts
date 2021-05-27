import {Money} from "./Money";
import {User} from "./User";

export class Bid {
    user: User;
    time: string;
    amount: Money;

    constructor(user: User, time: string, amount: Money) {
        this.user = user;
        this.time = time;
        this.amount = amount;
    }

    static fromDataToDomain(data: Bid) {
        return new Bid(
            User.fromDataToDomain(data.user),
            data.time,
            Money.fromDataToDomain(data.amount)
        );
    }
}