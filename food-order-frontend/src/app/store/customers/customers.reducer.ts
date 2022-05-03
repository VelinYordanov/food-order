import { combineReducers } from "@ngrx/store";
import { addressesReducer } from "./addresses/addresses.reducer";
import { cartReducer } from "./cart/cart.reducers";
import { customerOrdersReducer } from "./customer-orders/customer-orders.reducers";

export const customersReducer = combineReducers({
    addresses: addressesReducer,
    cart: cartReducer,
    customerOrders: customerOrdersReducer
})

export const customersStateKey = 'customers';