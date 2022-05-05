import { createFeatureSelector, createSelector } from "@ngrx/store";
import { RestaurantsState } from "../../models/restaurants-state";
import { restaurantsStateKey } from "../restaurants.reducers";

export const restaurantsState = createFeatureSelector<RestaurantsState>(restaurantsStateKey);

export const selectDiscountCodes = createSelector(
    restaurantsState,
    state => state.discountCodes
)