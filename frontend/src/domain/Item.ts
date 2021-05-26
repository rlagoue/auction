export class Item {
    id: string;
    name: string;
    description: string;


    constructor(id: string, name: string, description: string) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    static fromDataToDomain(item: Item) {
        return new Item(item.id, item.name, item.description);
    }
}