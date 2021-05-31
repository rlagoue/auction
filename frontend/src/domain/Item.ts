import {Money} from "./Money";
import {Bid} from "./Bid";

export class Item {
    static Null = new Item("", "", "", false);
    static totalCount = 0;

    id: string;
    name: string;
    description: string;
    bids: Bid[];
    isAutoBidActive: boolean;

    constructor(
        id: string,
        name: string,
        description: string,
        isAutoBidActive: boolean
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isAutoBidActive = isAutoBidActive;
        this.bids = [];
    }

    static fromDataToDomain(data: Item) {
        const item = new Item(
            data.id,
            data.name,
            data.description,
            data.isAutoBidActive
        );
        if (data.bids) {
            item.bids = data.bids.map(bid => Bid.fromDataToDomain(bid));
        }
        return item;
    }

    isNull = (): boolean => {
        return this === Item.Null;
    }

    getCurrentBid(): Money {
        if (this.bids.length === 0) {
            return Money.Null;
        }
        return [...this.bids].sort(
            (a, b) => b.amount.value - a.amount.value
        )[0].amount;
    }

    getStartBid(): Money {
        if (this.bids.length === 0) {
            return Money.Null;
        }
        return [...this.bids].sort(
            (a, b) => a.amount.value - b.amount.value
        )[0].amount;
    }
}