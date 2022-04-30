import { createAction, props } from "@ngrx/store";
import { EnumStoreData } from "../models/enum-store-data";

export const loadCitiesAction = createAction('[Enum] Load Cities');
export const loadCitiesRequestAction = createAction('[Enum] Load Cities Request');
export const loadCitiesSuccessAction = createAction('[Enum] Load Cities Success', props<{payload: EnumStoreData}>());
export const loadCitiesErrorAction = createAction('[Enum] Load Cities Error', props<{ payload: any }>());

export const loadAddressTypesAction = createAction('[Enum] Load AddressTypes');
export const loadAddressTypesRequestAction = createAction('[Enum] Load AddressTypes Request');
export const loadAddressTypesSuccessAction = createAction('[Enum] Load AddressTypes Success', props<{payload: EnumStoreData}>());
export const loadAddressTypesErrorAction = createAction('[Enum] Load AddressTypes Error', props<{ payload: any }>());

export const loadOrderStatusesAction = createAction('[Enum] Load Order Statuses');
export const loadOrderStatusesRequestAction = createAction('[Enum] Load Order Statuses Request');
export const loadOrderStatusesSuccessAction = createAction('[Enum] Load Order Statuses Success', props<{payload: EnumStoreData}>());
export const loadOrderStatusesErrorAction = createAction('[Enum] Load Order Statuses Error', props<{ payload: any }>());