import { createReducer, on } from "@ngrx/store";
import { loadAddressesSuccess } from "./addresses.actions";

export const initialState = []

export const addressesReducer = createReducer(
    initialState,
    on(loadAddressesSuccess, (state, { addresses }) => addresses),
);