import { createReducer, on } from "@ngrx/store";
import { loadAddressTypesSuccessAction, loadCitiesSuccessAction, loadOrderStatusesSuccessAction } from "./enums.actions";

const initialState = {
    cities: [],
    addressTypes: [],
    orderTypes: []
}

export const enumsReducer = createReducer(
    initialState,
    on(loadCitiesSuccessAction, (state, action) => ({ ...state, ...{ cities: action.payload } })),
    on(loadAddressTypesSuccessAction, (state, action) => ({ ...state, ...{ addressTypes: action.payload } })),
    on(loadOrderStatusesSuccessAction, (state, action) => ({ ...state, ...{ orderTypes: action.payload } }))
)