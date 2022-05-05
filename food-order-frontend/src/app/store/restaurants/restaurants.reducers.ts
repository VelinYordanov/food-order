import { state } from "@angular/animations";
import { createReducer, on } from "@ngrx/store";
import { deleteDiscountCodeSuccessAction } from "./discount-codes/discount-codes.actions";
import { discountCodesReducer } from "./discount-codes/discount-codes.reducers";
import { loadRestaurantErrorAction, loadRestaurantsSuccessAction, loadRestaurantSuccessAction } from "./restaurants.actions";

const initialState = {
    restaurants: [],
    currentRestaurant: null,
    discountCodes: []
}
export const restaurantsReducer = createReducer(
    initialState,
    on(loadRestaurantsSuccessAction, (state, action) => ({ ...state, ...{ restaurants: action.payload } })),
    on(loadRestaurantSuccessAction, (state, action) => ({ ...state, ...{ currentRestaurant: action.payload } })),
    on(loadRestaurantErrorAction, (state, action) => ({ ...state, ...{ currentRestaurant: null } })),
    on(deleteDiscountCodeSuccessAction, (state, action) => ({ ...state, ...{ discountCodes: discountCodesReducer(state.discountCodes, action) } }))
)

export const restaurantsStateKey = 'restaurants';