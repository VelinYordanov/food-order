import { Address } from "../../models/address";
import { CartState } from "./cart-state";
import { EnumState } from "./enum-state";

export interface CustomersState {
    addresses: Address[],
    enums: EnumState,
    cart: CartState
}