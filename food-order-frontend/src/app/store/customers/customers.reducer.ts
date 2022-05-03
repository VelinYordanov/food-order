import { ActionReducerMap } from "@ngrx/store";
import { addressesReducer } from "./addresses/addresses.reducer";
import { cartReducer } from "./cart/cart.reducers";
import { enumsReducer } from "./enums/enums.reducers";
import { CustomersState } from "../models/customers-state";

export const customersReducers: ActionReducerMap<CustomersState> = {
    addresses: addressesReducer,
    enums: enumsReducer,
    cart: cartReducer
}

export const customersStateKey = 'customers';