import { createAction, props } from "@ngrx/store";
import { DiscountCode } from "src/app/customers/models/discount-code";
import { DiscountCodeItem } from "src/app/restaurants/models/discount-code-item";
import { DeleteDiscountCodePayload } from "../../models/delete-discount-code-payload";

export const deleteDiscountCodePromptAction = createAction('[Discount Codes] Delete Discount Code Prompt', props<{ payload: DiscountCodeItem }>());
export const deleteDiscountCodeAction = createAction('[Discount Codes] Delete Discount Code', props<{ payload: DiscountCodeItem }>());
export const deleteDiscountCodeSuccessAction = createAction('[Discount Codes] Delete Discount Code Success', props<{ payload: DiscountCode }>());
export const deleteDiscountCodeErrorAction = createAction('[Discount Codes] Delete Discount Code Error', props<{ payload: any }>());