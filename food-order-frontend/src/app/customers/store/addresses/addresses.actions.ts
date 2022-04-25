import { createAction, props } from "@ngrx/store";
import { Address } from "../../models/address";

export const loadAddressesAction = createAction('[Addresses API] Load Addresses', props<{customerId: string}>());
export const loadAddressesSuccessAction = createAction('[Addresses API] Load Addresses Success', props<{ addresses: Address[] }>());
export const loadAddressesErrorAction = createAction('[Addresses API] Load Addresses Error', props<{ error: any }>());