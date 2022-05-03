import { createReducer, on } from "@ngrx/store";
import { loadRestaurantErrorAction, loadRestaurantsSuccessAction, loadRestaurantSuccessAction } from "./restaurants.actions";

const initialState = {
    restaurants: [],
    currentRestaurant: null
}
export const restaurantsReducer = createReducer(
    initialState,
    on(loadRestaurantsSuccessAction, (state, action) => ({ ...state, ...{ restaurants: action.payload } })),
    on(loadRestaurantSuccessAction, (state, action) => ({ ...state, ...{ currentRestaurant: action.payload } })),
    on(loadRestaurantErrorAction, (state, action) => ({ ...state, ...{ currentRestaurant: null } }))
)

export const restaurantsStateKey = 'restaurants';