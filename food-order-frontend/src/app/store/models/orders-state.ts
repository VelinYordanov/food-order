import { OrdersStateContent } from "./orders-state-content";

export interface OrdersState {
    orders: OrdersStateContent;
    totalPages: Number;
    totalElements: Number;
    last: boolean;
    numberOfElements: number;
    first: boolean;
    number: number;
    size: number;
}