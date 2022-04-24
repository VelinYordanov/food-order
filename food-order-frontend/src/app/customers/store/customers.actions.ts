import { createAction, props } from "@ngrx/store";
import { Address } from "../models/address";

export const loadAddressesAction = createAction('[Addresses API] Load Addresses', props<{customerId: string}>());
export const loadAddressesSuccess = createAction('[Addresses API] Load Addresses Success', props<{ addresses: Address[] }>());
export const loadAddressesError = createAction('[Addresses API] Load Addresses Success', props<{ error: any }>());