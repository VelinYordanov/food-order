import { createAction, props } from "@ngrx/store";
import { CartFood } from "src/app/restaurants/models/cart-food";
import { CartItem } from "src/app/restaurants/models/cart-item";
import { Address } from "../../models/address";
import { OrderRestaurant } from "../../models/order-restaurant";

export const selectRestaurantAction = createAction('[Cart] Select Restaurant', props<{ payload: OrderRestaurant }>());
export const selectAddressAction = createAction('[Cart] Select Address', props<{ payload: Address }>());
export const addFoodToCartAction = createAction('[Cart] Add Food', props<{ payload: CartItem }>());
export const removeFoodFromCartAction = createAction('[Cart] Remove Food', props<{ payload: CartItem }>());
export const loadCartAction = createAction('[Cart] Load Cart', props<{ payload: CartItem[] }>());
export const increaseFoodQuantityAction = createAction('[Cart] Increase Food Quantity', props<{ payload: CartFood }>());
export const decreaseFoodQuantityAction = createAction('[Cart] Decrease Food Quantity', props<{ payload: CartFood }>());
export const clearCartAction = createAction('[Cart] Clear');