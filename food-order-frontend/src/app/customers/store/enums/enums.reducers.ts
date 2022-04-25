import { createReducer, on } from "@ngrx/store";
import { loadAddressTypesSuccessAction, loadCitiesSuccessAction, loadOrderStatusesSuccessAction } from "./enums.actions";

const initialState = {
    cities: [],
    addressTypes: [],
    orderTypes: []
}

export const enumsReducer = createReducer(
    initialState,
    on(loadCitiesSuccessAction, (state, enumData) => ({ ...state, ...{ cities: enumData.enumData } })),
    on(loadAddressTypesSuccessAction, (state, enumData) => ({ ...state, ...{ addressTypes: enumData.enumData } })),
    on(loadOrderStatusesSuccessAction, (state, enumData) => ({ ...state, ...{ orderTypes: enumData.enumData } }))
)