import { createAction, props } from "@ngrx/store";
import { Address } from "../../../customers/models/address";
import { LoadCustomerAddressPayload } from "../../models/load-customer-address-payload";
import { UpdateAddressPayload } from "../../models/update-address-payload";

export const loadAddressesAction = createAction('[Addresses API] Load Addresses', props<{ payload: string }>());
export const loadAddressesSuccessAction = createAction('[Addresses API] Load Addresses Success', props<{ payload: Address[] }>());
export const loadAddressesErrorAction = createAction('[Addresses API] Load Addresses Error', props<{ payload: any }>());

export const createAddressAction = createAction('[Addresses API] Create Address', props<{ payload: { customerId: string, address: Address } }>());
export const createAddressSuccessAction = createAction('[Addresses API] Create Address Success', props<{ payload: any }>());
export const createAddressErrorAction = createAction('[Addresses API] Create Address Error', props<{ payload: any }>());

export const deleteAddressPromptAction = createAction('[Addresses API] Delete Address Prompt', props<{ payload: Address }>());
export const deleteAddressAction = createAction('[Addresses API] Delete Address', props<{ payload: Address }>());
export const deleteAddressSuccessAction = createAction('[Addresses API] Delete Address Success', props<{ payload: Address }>());
export const deleteAddressErrorAction = createAction('[Addresses API] Delete Address Error', props<{ payload: any }>());

export const loadCustomerAddressAction = createAction('[Addresses API] Load Customer Address', props<{ payload: LoadCustomerAddressPayload }>());
export const loadCustomerAddressSuccessAction = createAction('[Addresses API] Load Customer Address Success', props<{ payload: Address }>());
export const loadCustomerAddressErrorAction = createAction('[Addresses API] Load Customer Address Error', props<{ payload: any }>());

export const updateAddressAction = createAction('[Addresses API] Update Address', props<{ payload: UpdateAddressPayload }>());
export const updateAddressSuccessAction = createAction('[Addresses API] Update Address Success', props<{ payload: any }>());
export const updateAddressErrorAction = createAction('[Addresses API] Update Address Error', props<{ payload: any }>());