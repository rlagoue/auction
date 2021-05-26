import axios from "axios";
import {Item} from "../domain/Item";

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
}

const fetchItems = async (pageIndex: number): Promise<Item[]> => {
    const response  = await axiosInstance.get<ItemsFetchResponse>(
        "/item",
        {
            params: {
                pageIndex,
            }
        }
    );
    Item.totalCount = response.data.totalCount;
    return response.data.items;
}

export const services = {
    login,
    fetchItems,
}