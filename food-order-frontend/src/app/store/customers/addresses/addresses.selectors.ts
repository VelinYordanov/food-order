import { createFeatureSelector, createSelector } from "@ngrx/store";
import { selectRouteParam } from "src/app/store/router/router.selectors";
import { CustomersState } from "../../models/customers-state";
import { customersStateKey } from "../customers.reducer";

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
