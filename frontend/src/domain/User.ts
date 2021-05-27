export class User {
    static Null = new User("");
    username: string;


    constructor(username: string) {
        this.username = username;
    }

    static fromDataToDomain(data: User) {
        return new User(data.username);
    }

    isNull = (): boolean => this === User.Null
}