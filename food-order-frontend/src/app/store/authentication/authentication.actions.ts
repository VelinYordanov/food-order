import { createAction, props } from "@ngrx/store";
import { CustomerRegisterDto } from "src/app/home/models/customer-register-dto";
import { JwtToken } from "src/app/home/models/jwt-token";
import { LoginUser } from "src/app/home/models/login-user";
import { RestaurantRegisterDto } from "src/app/home/models/restaurant-register-dto";
import { User } from "../../shared/models/user";

export const loginCustomerAction = createAction('[Authentication] Login Customer', props<{ payload: LoginUser }>());
export const loginCustomerSuccessAction = createAction('[Authentication] Login Customer Success', props<{ payload: JwtToken }>());
export const loginCustomerErrorAction = createAction('[Authentication] Login Customer Error', props<{ payload: any }>());

export const loginRestaurantAction = createAction('[Authentication] Login Restaurant', props<{ payload: LoginUser }>());
export const loginRestaurantSuccessAction = createAction('[Authentication] Login Restaurant Success', props<{ payload: JwtToken }>());
export const loginRestaurantErrorAction = createAction('[Authentication] Login Restaurant Error', props<{ payload: any }>());

export const registerCustomerAction = createAction('[Authentication] Register Customer', props<{ payload: CustomerRegisterDto }>());
export const registerCustomerSuccessAction = createAction('[Authentication] Register Customer Success', props<{ payload: JwtToken }>());
export const registerCustomerErrorAction = createAction('[Authentication] Register Customer Error', props<{ payload: any }>());

export const registerRestaurantAction = createAction('[Authentication] Register Restaurant', props<{ payload: RestaurantRegisterDto }>());
export const registerRestaurantSuccessAction = createAction('[Authentication] Register Restaurant Success', props<{ payload: JwtToken }>());
export const registerRestaurantErrorAction = createAction('[Authentication] Register Restaurant Error', props<{ payload: any }>());

export const updateUserAction = createAction('[Authentication] Update User', props<{ payload: User }>());