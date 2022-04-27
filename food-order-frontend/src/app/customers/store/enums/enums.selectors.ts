import { state } from "@angular/animations";
import { createFeatureSelector, createSelector } from "@ngrx/store";
import { customersStateKey } from "../customers.reducer";
import { CustomersState } from "../models/customers-state";

const customersSelector = createFeatureSelector<CustomersState>(customersStateKey);

const enumsSelector = createSelector(
    customersSelector,
    state => state.enums
)

export const citiesSelector = createSelector(
    enumsSelector,
    state => state.cities
)

export const addressTypesSelector = createSelector(
    enumsSelector,
    state => state.addressTypes
)

export const orderTypesSelector = createSelector(
    enumsSelector,
    state => state.orderTypes
)

export const cityByIdSelector = id =>
    createSelector(
        citiesSelector,
        state => state.find(city => city.id === id)
    )

export const addressTypeByIdSelector = id =>
    createSelector(
        addressTypesSelector,
        state => state.find(address => address.id === id)
    )