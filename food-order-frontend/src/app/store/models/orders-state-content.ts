import { Order } from "src/app/customers/models/order";

export interface OrdersStateContent {
    [key: string]: Order[];
}