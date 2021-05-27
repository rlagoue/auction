export class User {
    username: string;

    constructor(username: string) {
        this.username = username;
    }

    static fromDataToDomain(data: User) {
        return new User(data.username);
    }
}