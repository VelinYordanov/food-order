import { createReducer, on } from "@ngrx/store";
import { DiscountCodeItem } from "src/app/restaurants/models/discount-code-item";
import { deleteDiscountCodeSuccessAction } from "./discount-codes.actions";

const initialState: DiscountCodeItem[] = [];

export const discountCodesReducer = createReducer(
    initialState,
    on(deleteDiscountCodeSuccessAction, (state, action) => state.filter(dc => dc.id === action.payload.id))
);