import { combineReducers } from "@ngrx/store";
import { addressesReducer } from "./addresses/addresses.reducer";
import { cartReducer } from "./cart/cart.reducers";

export const customersReducer = combineReducers({
    addresses: addressesReducer,
    cart: cartReducer
})

export const customersStateKey = 'customers';