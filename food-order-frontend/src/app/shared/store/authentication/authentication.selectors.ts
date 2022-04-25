import { createFeatureSelector } from "@ngrx/store";
import { userStateKey } from "./authentication.reducer";

export const loggedInUserSelector = createFeatureSelector(userStateKey);