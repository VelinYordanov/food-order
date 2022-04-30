import { createFeatureSelector, createSelector } from "@ngrx/store";
import { CartState } from "../models/cart-state";

const cartStateSelector = createFeatureSelector<CartState>('cart');

export const selectedItemsSelector = createSelector(
    cartStateSelector,
    state => state.selectedItems
)

export const cartItemsSumSelector = createSelector(
    selectedItemsSelector,
    state => state.reduce((sum, current) => sum + current.quantity, 0)
)