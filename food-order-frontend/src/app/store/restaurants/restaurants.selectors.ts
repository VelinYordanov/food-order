import { createFeatureSelector, createSelector } from "@ngrx/store";
import { RestaurantsState } from "../models/restaurants-state";
import { restaurantsStateKey } from "./restaurants.reducers";

const restaurantsSelector = createFeatureSelector<RestaurantsState>(restaurantsStateKey);

export const selectRestaurants = createSelector(
    restaurantsSelector,
    state => state.restaurants
);

export const selectCurrentRestaurant = createSelector(
    restaurantsSelector,
    state => state.currentRestaurant
);

export const selectRestaurantCategories = createSelector(
    selectCurrentRestaurant,
    state => state.categories
)

export const selectRestaurantFoods = createSelector(
    selectCurrentRestaurant,
    state => state.foods
)