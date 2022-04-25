import { createAction, props } from "@ngrx/store";
import { EnumData } from "src/app/shared/models/enum-data";

export const loadCitiesAction = createAction('[Load Enum] Load Cities');
export const loadCitiesSuccessAction = createAction('[Load Enum] Load Cities Success', props<{enumData: EnumData[]}>());
export const loadCitiesErrorAction = createAction('[Load Enum] Load Cities Error', props<{ error: any }>());

export const loadAddressTypesAction = createAction('[Load Enum] Load AddressTypes');
export const loadAddressTypesSuccessAction = createAction('[Load Enum] Load AddressTypes Success', props<{enumData: EnumData[]}>());
export const loadAddressTypesErrorAction = createAction('[Load Enum] Load AddressTypes Error', props<{ error: any }>());

export const loadOrderStatusesAction = createAction('[Load Enum] Load Order Statuses');
export const loadOrderStatusesSuccessAction = createAction('[Load Enum] Load Order Statuses Success', props<{enumData: EnumData[]}>());
export const loadOrderStatusesErrorAction = createAction('[Load Enum] Load Order Statuses Error', props<{ error: any }>());