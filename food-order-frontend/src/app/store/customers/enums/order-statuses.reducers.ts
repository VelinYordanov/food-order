import { createReducer, on } from "@ngrx/store";
import { EnumStoreData } from "../../models/enum-store-data";
import { loadAddressTypesErrorAction, loadAddressTypesRequestAction, loadAddressTypesSuccessAction, loadCitiesErrorAction, loadCitiesRequestAction, loadCitiesSuccessAction, loadOrderStatusesErrorAction, loadOrderStatusesRequestAction, loadOrderStatusesSuccessAction } from "./enums.actions";

const initialState: EnumStoreData = {
    isLoading: false,
    entities: []
}

export const orderStatusesReducer = createReducer(
    initialState,
    on(loadOrderStatusesRequestAction, (state, action) => ({ ...state, ...{ isLoading: true } })),
    on(loadOrderStatusesSuccessAction, (state, action) => action.payload),
    on(loadOrderStatusesErrorAction, (state, action) => ({ ...state, ...{ isLoading: false } })),
)