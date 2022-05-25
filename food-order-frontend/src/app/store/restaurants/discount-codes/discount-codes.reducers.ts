import { createReducer, on } from "@ngrx/store";
import { DiscountCodeItem } from "src/app/restaurants/models/discount-code-item";
import { createDiscountCodeSuccessAction, deleteDiscountCodeSuccessAction, loadDiscountCodesSuccesAction } from "./discount-codes.actions";

const initialState: DiscountCodeItem[] = [];

export const discountCodesReducer = createReducer(
    initialState,
    on(loadDiscountCodesSuccesAction, (state, action) => action.payload),
    on(deleteDiscountCodeSuccessAction, (state, action) => state.filter(dc => dc.id !== action.payload.id)),
    on(createDiscountCodeSuccessAction, (state, action) => [...state, action.payload])
);