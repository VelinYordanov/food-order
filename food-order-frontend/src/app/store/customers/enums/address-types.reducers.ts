import { createReducer, on } from "@ngrx/store";
import { EnumStoreData } from "../models/enum-store-data";
import { loadAddressTypesErrorAction, loadAddressTypesRequestAction, loadAddressTypesSuccessAction, loadCitiesErrorAction, loadCitiesRequestAction, loadCitiesSuccessAction } from "./enums.actions";

const initialState: EnumStoreData = {
    isLoading: false,
    entities: []
}

export const addressTypesReducer = createReducer(
    initialState,
    on(loadAddressTypesRequestAction, (state, action) => ({ ...state, ...{ isLoading: true } })),
    on(loadAddressTypesSuccessAction, (state, action) => action.payload),
    on(loadAddressTypesErrorAction, (state, action) => ({ ...state, ...{ isLoading: false } })),
)