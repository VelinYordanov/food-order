import { createFeatureSelector, createSelector } from "@ngrx/store";
import { selectQueryParam, selectRouteParam } from "src/app/store/router/router.selectors";
import { customersStateKey } from "../customers.reducer";
import { CustomersState } from "../models/customers-state";

export const selectCustomers = createFeatureSelector<CustomersState>(customersStateKey);

export const selectAddresses = createSelector(
    selectCustomers,
    state => state.addresses
)

export const selectAddressById = createSelector(
    selectAddresses,
    selectRouteParam('id'),
    (addresses, id) => addresses.find(address => address.id === id)
)
