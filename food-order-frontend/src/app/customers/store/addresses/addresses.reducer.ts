import { createReducer, on } from "@ngrx/store";
import { Address } from "../../models/address";
import { deleteAddressSuccessAction, loadAddressesSuccessAction, loadCustomerAddressSuccessAction } from "./addresses.actions";

export const initialState: Address[] = [];

export const addressesReducer = createReducer(
    initialState,
    on(loadAddressesSuccessAction, (state, { payload }) => payload),
    on(deleteAddressSuccessAction, (state, { payload }) => state.filter(address => address.id !== payload.id)),
    on(loadCustomerAddressSuccessAction, (state, { payload }) => state.map(address => address.id === payload.id ? payload : address))
);