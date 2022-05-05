import { createAction, props } from "@ngrx/store";
import { DiscountCode } from "src/app/customers/models/discount-code";
import { DiscountCodeItem } from "src/app/restaurants/models/discount-code-item";
import { CreateDiscountCodePayload } from "../../models/create-discount-code-payload";
import { DiscountCodeEditPayload } from "../../models/discount-code-edit-payload";

export const loadDiscountCodesAction = createAction('[Discount Codes] Load Discount Codes', props<{ payload: string }>());
export const loadDiscountCodesSuccesAction = createAction('[Discount Codes] Load Discount Codes Success', props<{ payload: DiscountCodeItem[] }>());
export const loadDiscountCodesErrorAction = createAction('[Discount Codes] Load Discount Codes Error', props<{ payload: any }>());

export const createDiscountCodeAction = createAction('[Discount Codes] Create Discount Code', props<{ payload: CreateDiscountCodePayload }>());
export const createDiscountCodeSuccessAction = createAction('[Discount Codes] Create Discount Code Success');
export const createDiscountCodeErrorAction = createAction('[Discount Codes] Create Discount Code Error', props<{ payload: any }>());

export const deleteDiscountCodePromptAction = createAction('[Discount Codes] Delete Discount Code Prompt', props<{ payload: DiscountCodeItem }>());
export const deleteDiscountCodeAction = createAction('[Discount Codes] Delete Discount Code', props<{ payload: DiscountCodeItem }>());
export const deleteDiscountCodeSuccessAction = createAction('[Discount Codes] Delete Discount Code Success', props<{ payload: DiscountCode }>());
export const deleteDiscountCodeErrorAction = createAction('[Discount Codes] Delete Discount Code Error', props<{ payload: any }>());

export const editDiscountCodeAction = createAction('[Discount Codes] Edit Discount Code', props<{ payload: DiscountCodeEditPayload }>());
export const editDiscountCodeSuccessAction = createAction('[Discount Codes] Edit Discount Code Success', props<{ payload: DiscountCodeItem }>());
export const editDiscountCodeErrorAction = createAction('[Discount Codes] Edit Discount Code Error', props<{ payload: any }>());
