import { ActionReducerMap } from "@ngrx/store";
import { addressesReducer } from "./addresses/addresses.reducer";
import { enumsReducer } from "./enums/enums.reducers";
import { CustomersState } from "./models/customers-state";

export const customersReducers: ActionReducerMap<CustomersState> = {
    addresses: addressesReducer,
    enums: enumsReducer
}

export const customersStateKey = 'customers';