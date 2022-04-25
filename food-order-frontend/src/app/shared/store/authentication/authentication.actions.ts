import { createAction, props } from "@ngrx/store";
import { JwtToken } from "src/app/home/models/jwt-token";
import { LoginUser } from "src/app/home/models/login-user";
import { User } from "../../models/user";

export const loginCustomerAction = createAction('[Authentication] Login Customer', props<{ payload: LoginUser }>());
export const loginCustomerSuccessAction = createAction('[Authentication] Login Customer Success', props<{ payload: JwtToken }>());
export const loginCustomerErrorAction = createAction('[Authentication] Login Customer Error', props<{ payload: any }>());

export const loginRestaurantAction = createAction('[Authentication] Login Restaurant', props<{ payload: LoginUser }>());
export const loginRestaurantSuccessAction = createAction('[Authentication] Login Restaurant Success', props<{ payload: JwtToken }>());
export const loginRestaurantErrorAction = createAction('[Authentication] Login Restaurant Error', props<{ payload: any }>());

export const updateUserAction = createAction('[Authentication] Update User', props<{ payload: User }>());