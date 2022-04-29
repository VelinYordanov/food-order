import { state } from "@angular/animations";
import { createReducer, on } from "@ngrx/store";
import { EnumState } from "../models/enum-state";
import { loadAddressTypesAction, loadAddressTypesErrorAction, loadAddressTypesRequestAction, loadAddressTypesSuccessAction, loadCitiesAction, loadCitiesErrorAction, loadCitiesRequestAction, loadCitiesSuccessAction, loadOrderStatusesAction, loadOrderStatusesErrorAction, loadOrderStatusesSuccessAction } from "./enums.actions";

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

export const enumsReducer = createReducer(
    initialState,
    on(loadCitiesRequestAction, (state, action) => ({ ...state, ... { cities: { ...state.cities, ... { isLoading: true } } } })),
    on(loadAddressTypesRequestAction, (state, action) => ({ ...state, ... { addressTypes: { ...state.addressTypes, ... { isLoading: true } } } })),
    on(loadOrderStatusesAction, (state, action) => ({ ...state, ... { orderTypes: { ...state.orderTypes, ... { isLoading: true } } } })),
    on(loadCitiesSuccessAction, (state, action) => ({ ...state, ...{ cities: action.payload } })),
    on(loadAddressTypesSuccessAction, (state, action) => ({ ...state, ...{ addressTypes: action.payload } })),
    on(loadOrderStatusesSuccessAction, (state, action) => ({ ...state, ...{ orderTypes: action.payload } })),
    on(loadCitiesErrorAction, (state, action) => ({ ...state, ... { cities: { ...state.cities, ... { isLoading: false } } } })),
    on(loadAddressTypesErrorAction, (state, action) => ({ ...state, ... { addressTypes: { ...state.addressTypes, ... { isLoading: false } } } })),
    on(loadOrderStatusesErrorAction, (state, action) => ({ ...state, ... { orderTypes: { ...state.orderTypes, ... { isLoading: false } } } })),
)