import { Order } from "src/app/customers/models/order";
import { Page } from "src/app/shared/models/page";

export interface CustomerOrdersState {
    orders: Page<Order>;
    currentOrder: Order
}