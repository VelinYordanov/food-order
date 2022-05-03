import { createFeatureSelector, createSelector } from "@ngrx/store";
import { CustomersState } from "../../models/customers-state";
import { customersStateKey } from "../customers.reducer";

const selectCustomersState = createFeatureSelector<CustomersState>(customersStateKey);

export const selectCustomerOrdersState = createSelector(
    selectCustomersState,
    state => state.customerOrders
)

export const selectCustomerOrders = createSelector(
    selectCustomerOrdersState,
    state => state.orders
);

export const selectCurrentCustomerOrder = createSelector(
    selectCustomerOrdersState,
    state => state.currentOrder
)