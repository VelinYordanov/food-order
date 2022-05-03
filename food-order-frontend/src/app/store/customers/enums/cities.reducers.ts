import { createReducer, on } from "@ngrx/store";
import { EnumStoreData } from "../../models/enum-store-data";
import { loadCitiesErrorAction, loadCitiesRequestAction, loadCitiesSuccessAction } from "./enums.actions";

const initialState: EnumStoreData = {
    isLoading: false,
    entities: []
}

export const citiesReducer = createReducer(
    initialState,
    on(loadCitiesRequestAction, (state, action) => ({ ...state, ...{ isLoading: true } })),
    on(loadCitiesSuccessAction, (state, action) => action.payload),
    on(loadCitiesErrorAction, (state, action) => ({ ...state, ...{ isLoading: false } })),
)