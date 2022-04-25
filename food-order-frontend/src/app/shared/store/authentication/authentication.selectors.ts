import { createFeatureSelector } from "@ngrx/store";
import { User } from "../../models/user";
import { userStateKey } from "./authentication.reducer";

export const loggedInUserSelector = createFeatureSelector<User>(userStateKey);