import { createReducer, on } from "@ngrx/store";
import { loadAddressesSuccessAction } from "./addresses.actions";

export const initialState = []

export const addressesReducer = createReducer(
    initialState,
    on(loadAddressesSuccessAction, (state, { payload }) => payload),
);