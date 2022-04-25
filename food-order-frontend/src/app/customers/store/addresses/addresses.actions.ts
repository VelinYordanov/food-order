import { createAction, props } from "@ngrx/store";
import { Address } from "../../models/address";

export const loadAddressesAction = createAction('[Addresses API] Load Addresses', props<{payload: string}>());
export const loadAddressesSuccessAction = createAction('[Addresses API] Load Addresses Success', props<{ payload: Address[] }>());
export const loadAddressesErrorAction = createAction('[Addresses API] Load Addresses Error', props<{ payload: any }>());