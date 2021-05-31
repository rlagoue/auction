import {Money} from "./Money";
import {User} from "./User";

export class Bid {
    static Null = new Bid("", new User(""), "", Money.Null);
    id: string;
    user: User;
    time: string;
    amount: Money;

    constructor(id: string, user: User, time: string, amount: Money) {
        this.id = id;
        this.user = user;
        this.time = time;
        this.amount = amount;
    }

    static fromDataToDomain(data: Bid) {
        return new Bid(
            data.id,
            User.fromDataToDomain(data.user),
            data.time,
            Money.fromDataToDomain(data.amount)
        );
    }
}