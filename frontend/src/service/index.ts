import axios from "axios";
import {Item} from "../domain/Item";
import {User} from "../domain/User";
import {Settings} from "../domain/Settings";

const axiosInstance = axios.create({
    baseURL: <string>import.meta.env.VITE_REST_API_BASE_URL
})

const login = async (
    username: string,
    password: string
): Promise<string> => {
    const response = await axiosInstance.post<string>(
        "/authenticate",
        {
            username: username,
            password: password
        }
    );
    if (!response.data) {
        throw new Error("loginFailed");
    }
    return response.data;
};

type ItemsFetchResponse = {
    totalCount: number,
    items: Item[],
};

const fetchItems = async (pageIndex: number): Promise<Item[]> => {
    const response = await axiosInstance.get<ItemsFetchResponse>(
        "/item",
        {
            params: {
                pageIndex,
            }
        }
    );
    Item.totalCount = response.data.totalCount;
    return response.data.items;
};

const fetchItemById = async (id: string): Promise<Item> => {
    const response = await axiosInstance.get<Item>(
        "/item/" + id
    );
    return response.data;
};

const addItem = async (name: string, description: string, startBid: number): Promise<string> => {
    const response = await axiosInstance.post<string>(
        "/item",
        {
            name,
            description,
            startBid,
        }
    )
    return response.data;
};

const makeBid = async (
    user: User,
    itemId: string,
    bid: number
): Promise<string> => {
    const response = await axiosInstance.post<string>(
        "/item/" + itemId + "/bid",
        {
            user,
            bid,
        }
    );
    return response.data;
}


const fetchSettings = async (user: User): Promise<Settings> => {
    const response = await axiosInstance.get<Settings>(
        "/user/" + user.username + "/settings"
    );
    return response.data;
}

const saveSettings = async (settings: Settings, user: User) => {
    const response = await axiosInstance.put(
        "/user/" + user.username + "/settings",
        {
            maxBidAmount: settings.maxBidAmount,
        },
    );
    return response.data;
}

export const services = {
    login,
    fetchItems,
    fetchItemById,
    addItem,
    makeBid,
    fetchSettings,
    saveSettings,
}