import { createReducer, on } from "@ngrx/store";
import { User } from "../../models/user";
import { updateUserAction } from "./authentication.actions";

const initialState: User = null;

export const userStateKey = 'user';

export const authenticationReducer = createReducer(
    initialState,
    on(updateUserAction, (state, user) => user.user)
)