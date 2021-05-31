export class Session {
    username: string;

    constructor(username: string) {
        this.username = username;
    }

    isAdmin = (): boolean => this.username === "admin";
}