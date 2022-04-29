import { createFeatureSelector, createSelector } from "@ngrx/store";
import { EnumData } from "src/app/shared/models/enum-data";
import { customersStateKey } from "../customers.reducer";
import { CustomersState } from "../models/customers-state";
import { EnumStoreData } from "../models/enum-store-data";

const customersSelector = createFeatureSelector<CustomersState>(customersStateKey);

const isLoading = (state: EnumStoreData) => state.isLoading;
const getEntities = (state: EnumStoreData) => state.entities;
const getEntityById = (state: EnumStoreData, id) => state.entities.find(data => data.id === id);

const enumsSelector = createSelector(
    customersSelector,
    state => state.enums
)

export const citiesEnumStoreDataSelector = createSelector(
    enumsSelector,
    state => state.cities,
)

export const addressTypesEnumStoreDataSelector = createSelector(
    enumsSelector,
    state => state.addressTypes
)

export const orderTypesEnumStoreDataSelector = createSelector(
    enumsSelector,
    state => state.orderTypes,
)

export const citiesSelector = createSelector(
    citiesEnumStoreDataSelector,
    getEntities
)

export const addressTypesSelector = createSelector(
    addressTypesEnumStoreDataSelector,
    getEntities
)

export const orderTypesSelector = createSelector(
    orderTypesEnumStoreDataSelector,
    getEntities
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

export const citiesLoadingSelector = createSelector(
    citiesEnumStoreDataSelector,
    isLoading
)

export const addressTypesLoadingSelector = createSelector(
    citiesEnumStoreDataSelector,
    isLoading
)

export const orderStatusesLoadingSelector = createSelector(
    orderTypesEnumStoreDataSelector,
    isLoading
)