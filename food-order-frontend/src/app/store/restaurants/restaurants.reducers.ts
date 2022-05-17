import { createReducer, on } from "@ngrx/store";
import { RestaurantsState } from "../models/restaurants-state";
import { deleteDiscountCodeSuccessAction } from "./discount-codes/discount-codes.actions";
import { discountCodesReducer } from "./discount-codes/discount-codes.reducers";
import { loadMonthlyGraphSuccesAction, loadYearlyGraphSuccesAction } from "./graphs/graphs.actions";
import { graphsReducer } from "./graphs/graphs.reducers";
import { addCategoryToRestaurantSuccessAction, addFoodToRestaurantSuccessAction, loadRestaurantErrorAction, loadRestaurantsSuccessAction, loadRestaurantSuccessAction } from "./restaurants.actions";

const initialState: RestaurantsState = {
    restaurants: [],
    currentRestaurant: null,
    discountCodes: [],
    graphData: {
        monthlyGraphData: {},
        yearlyGraphData: {}
    }
}
export const restaurantsReducer = createReducer(
    initialState,
    on(loadRestaurantsSuccessAction, (state, action) => ({ ...state, ...{ restaurants: action.payload } })),
    on(loadRestaurantSuccessAction, (state, action) => ({ ...state, ...{ currentRestaurant: action.payload } })),
    on(loadRestaurantErrorAction, (state, action) => ({ ...state, ...{ currentRestaurant: null } })),
    on(deleteDiscountCodeSuccessAction, (state, action) => ({ ...state, ...{ discountCodes: discountCodesReducer(state.discountCodes, action) } })),
    on(loadMonthlyGraphSuccesAction,
        loadYearlyGraphSuccesAction,
        (state, action) => ({ ...state, graphData: graphsReducer(state.graphData, action) })),
    on(addFoodToRestaurantSuccessAction, (state, action) => ({ ...state, currentRestaurant: { ...state.currentRestaurant, foods: [...state.currentRestaurant.foods, action.payload] } })),
    on(addCategoryToRestaurantSuccessAction, (state, action) => ({ ...state, currentRestaurant: { ...state.currentRestaurant, categories: [...state.currentRestaurant.categories, action.payload] } }))
)

export const restaurantsStateKey = 'restaurants';