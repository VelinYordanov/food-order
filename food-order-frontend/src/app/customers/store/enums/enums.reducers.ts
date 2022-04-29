import { state } from "@angular/animations";
import { combineReducers, createReducer, on } from "@ngrx/store";
import { EnumState } from "../models/enum-state";
import { addressTypesReducer } from "./address-types.reducers";
import { citiesReducer } from "./cities.reducers";
import { loadAddressTypesAction, loadAddressTypesErrorAction, loadAddressTypesRequestAction, loadAddressTypesSuccessAction, loadCitiesAction, loadCitiesErrorAction, loadCitiesRequestAction, loadCitiesSuccessAction, loadOrderStatusesAction, loadOrderStatusesErrorAction, loadOrderStatusesSuccessAction } from "./enums.actions";
import { orderStatusesReducer } from "./order-statuses.reducers";

const initialState: EnumState = {
    cities: {
        entities: [],
        isLoading: false
    },
    addressTypes: {
        entities: [],
        isLoading: false
    },
    orderTypes: {
        entities: [],
        isLoading: false
    }
}

export const enumsReducer = combineReducers({
    cities: citiesReducer,
    addressTypes: addressTypesReducer,
    orderTypes: orderStatusesReducer
});