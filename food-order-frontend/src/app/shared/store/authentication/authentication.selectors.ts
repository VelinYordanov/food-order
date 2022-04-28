import { createFeatureSelector, createSelector } from "@ngrx/store";
import { selectRouteParam } from "src/app/store/router/router.selectors";
import { User } from "../../models/user";
import { userStateKey } from "./authentication.reducer";

export const loggedInUserSelector = createFeatureSelector<User>(userStateKey);

export const loggedInUserWithRouteParameter = (parameter: string) =>
    createSelector(
        loggedInUserSelector,
        selectRouteParam(parameter),
        (user, routeParameter) => ({ userId: user.id, param: routeParameter })
    )