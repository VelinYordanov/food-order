import { createFeatureSelector, createSelector } from "@ngrx/store"
import { RestaurantsState } from "../../models/restaurants-state";
import { restaurantsStateKey } from "../restaurants.reducers";

export const restaurantsState = createFeatureSelector<RestaurantsState>(restaurantsStateKey);

const selectGraphs = createSelector(
    restaurantsState,
    state => state.graphData
)

const selectMonthlyGraphs = createSelector(
    selectGraphs,
    state => state.monthlyGraphData
);

const selectYearlyGraphs = createSelector(
    selectGraphs,
    state => state.yearlyGraphData
);


export const selectMonthlyGraphData = function (year: number, month: number) {
    return createSelector(
        selectMonthlyGraphs,
        state => state[`${year}-${month}`] || []
    )
}

export const selectYearlyGraphData = function (year: number) {
    return createSelector(
        selectYearlyGraphs,
        state => state[year] || []
    )
}