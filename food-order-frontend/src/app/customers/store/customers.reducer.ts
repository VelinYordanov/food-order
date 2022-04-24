import { addressesReducer } from "./addresses/addresses.reducer";

export const customersReducers = {
    addresses: addressesReducer
}

export const customersStateKey = 'customers';