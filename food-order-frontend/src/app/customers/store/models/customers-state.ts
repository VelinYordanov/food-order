import { Address } from "../../models/address";
import { EnumState } from "./enum-state";

export interface CustomersState {
    addresses: Address[],
    enums: EnumState
}