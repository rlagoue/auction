import {defineStore} from "pinia";
import {Session} from "../domain/Session";
import {services} from "../service";
import {Item} from "../domain/Item";
import {User} from "../domain/User";
import {Settings} from "../domain/Settings";

export interface State {
    publicRoutes: string[],
    session: Session | null,
    whereToGoAfterLogin: string,
    currentPage: {
        title: string,
    },
}


export const useStore = defineStore({
    id: "main",
    state: (): State => ({
        publicRoutes: ["login"],
        session: null,
        whereToGoAfterLogin: "",
        currentPage: {
            title: "",
        },
    }),
    getters: {
        isLoggedIn(): boolean {
            return this.session !== null;
        },
        title(): string {
            return this.currentPage.title;
        },
        isAdmin(): boolean {
            if (!this.session) {
                return false;
            }
            return this.session.isAdmin();
        },
        currentUser(): User {
            if (!this.session) {
                return User.Null;
            }
            return new User(this.session.username);
        }
    },
    actions: {
        nextAfterLogin(path: string) {
            this.whereToGoAfterLogin = path;
        },
        async signIn(username: string, password: string): Promise<any> {
            const token = await services.login(username, password);
            this.session = new Session(username, token);
        },
        async fetchItems(pageIndex: number): Promise<Item[]> {
            const items = await services.fetchItems(pageIndex);
            return items.map(item => Item.fromDataToDomain(item));
        },
        setCurrentPage(title: string) {
            this.currentPage.title = title;
        },
        logout() {
            this.session = null;
        },
        async fetchItemById(id: string): Promise<Item> {
            const item = await services.fetchItemById(id);
            return Item.fromDataToDomain(item);
        },
        async addItem(name: string, description: string, startBid: number): Promise<string> {
            return await services.addItem(name, description, startBid);
        },
        async makeBid(itemId: string, bidValue: number): Promise<string> {
            return await services.makeBid(
                this.currentUser,
                itemId,
                bidValue
            );
        },
        async fetchSettings(): Promise<Settings> {
            return await services.fetchSettings(this.currentUser);
        },
        async saveSettings(settings: Settings): Promise<void> {
            await services.saveSettings(settings, this.currentUser);
        },
        async activateAutoBid(itemId: string): Promise<void> {
            await services.activateAutoBid(itemId, this.currentUser);
        },
        async deactivateAutoBid(itemId: string): Promise<void> {
            await services.deactivateAutoBid(itemId, this.currentUser);
        }
    }
});