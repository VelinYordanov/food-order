import { createAction, props } from "@ngrx/store";
import { EnumStoreData } from "../models/enum-store-data";

export const loadCitiesAction = createAction('[Load Enum] Load Cities');
export const loadCitiesRequestAction = createAction('[Load Enum] Load Cities Request');
export const loadCitiesSuccessAction = createAction('[Load Enum] Load Cities Success', props<{payload: EnumStoreData}>());
export const loadCitiesErrorAction = createAction('[Load Enum] Load Cities Error', props<{ payload: any }>());

export const loadAddressTypesAction = createAction('[Load Enum] Load AddressTypes');
export const loadAddressTypesRequestAction = createAction('[Load Enum] Load AddressTypes Request');
export const loadAddressTypesSuccessAction = createAction('[Load Enum] Load AddressTypes Success', props<{payload: EnumStoreData}>());
export const loadAddressTypesErrorAction = createAction('[Load Enum] Load AddressTypes Error', props<{ payload: any }>());

export const loadOrderStatusesAction = createAction('[Load Enum] Load Order Statuses');
export const loadOrderStatusesRequestAction = createAction('[Load Enum] Load Order Statuses Request');
export const loadOrderStatusesSuccessAction = createAction('[Load Enum] Load Order Statuses Success', props<{payload: EnumStoreData}>());
export const loadOrderStatusesErrorAction = createAction('[Load Enum] Load Order Statuses Error', props<{ payload: any }>());