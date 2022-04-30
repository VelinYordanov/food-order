import { createFeatureSelector, createSelector } from "@ngrx/store";
import { selectRouteParam } from "src/app/store/router/router.selectors";
import { User } from "../../models/user";
import { userStateKey } from "./authentication.reducer";

export const loggedInUserSelector = createFeatureSelector<User>(userStateKey);

export const loggedInUserWithRouteParameter = (parameter: string) =>
    createSelector(
        loggedInUserIdSelector,
        selectRouteParam(parameter),
        (userId, routeParameter) => ({ userId, param: routeParameter })
    )

export const loggedInUserIdSelector = createSelector(
    loggedInUserSelector,
    user => user.id
)