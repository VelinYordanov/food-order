import { createReducer, on } from "@ngrx/store";
import { Food } from "src/app/restaurants/models/food";
import { RestaurantsState } from "../models/restaurants-state";
import { deleteDiscountCodeSuccessAction } from "./discount-codes/discount-codes.actions";
import { discountCodesReducer } from "./discount-codes/discount-codes.reducers";
import { loadMonthlyGraphSuccesAction, loadYearlyGraphSuccesAction } from "./graphs/graphs.actions";
import { graphsReducer } from "./graphs/graphs.reducers";
import { addCategoryToRestaurantSuccessAction, addFoodToRestaurantSuccessAction, deleteCategoryFromRestaurantSuccessAction, deleteFoodFromRestaurantAction, deleteFoodFromRestaurantSuccessAction, editRestaurantFoodAction, editRestaurantFoodSuccessAction, loadRestaurantErrorAction, loadRestaurantsSuccessAction, loadRestaurantSuccessAction } from "./restaurants.actions";

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
    on(deleteFoodFromRestaurantSuccessAction, (state, action) => ({ ...state, currentRestaurant: { ...state.currentRestaurant, foods: state.currentRestaurant.foods.filter(f => f.id !== action.payload.data) } })),
    on(editRestaurantFoodSuccessAction, (state, action) => editFood(state, action)),
    on(addCategoryToRestaurantSuccessAction, (state, action) => ({ ...state, currentRestaurant: { ...state.currentRestaurant, categories: [...state.currentRestaurant.categories, action.payload.data] } })),
    on(deleteCategoryFromRestaurantSuccessAction, (state, action) => ({ ...state, currentRestaurant: { ...state.currentRestaurant, categories: state.currentRestaurant.categories.filter(c => c.id !== action.payload.id) } }))
)

function editFood(state: RestaurantsState, action: { payload: Food }): RestaurantsState {
    const food = action.payload;
    const updatedFoods = [...state.currentRestaurant.foods.filter(f => f.id !== food.id), food];
    return { ...state, currentRestaurant: { ...state.currentRestaurant, foods: updatedFoods } };
}

export const restaurantsStateKey = 'restaurants';