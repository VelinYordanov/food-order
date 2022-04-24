import { createFeatureSelector, createSelector } from "@ngrx/store";
import { customersStateKey } from "../customers.reducer";
import { CustomersState } from "../models/customers-state";

export const selectCustomers = createFeatureSelector<CustomersState>(customersStateKey);

export const selectAddresses = createSelector(
    selectCustomers,
    state => state.addresses
)
