import { Address } from "src/app/customers/models/address";
import { CartState } from "./cart-state";
import { CustomerOrdersState } from "./customer-orders-state";
import { EnumState } from "./enum-state";

export interface CustomersState {
    addresses: Address[],
    enums: EnumState,
    cart: CartState,
    customerOrders: CustomerOrdersState
}