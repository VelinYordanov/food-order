import { createAction, props } from "@ngrx/store";
import { Address } from "src/app/customers/models/address";
import { DiscountCode } from "src/app/customers/models/discount-code";
import { Order } from "src/app/customers/models/order";
import { OrderCreate } from "src/app/customers/models/order-create";
import { OrderRestaurant } from "src/app/customers/models/order-restaurant";
import { CartFood } from "src/app/restaurants/models/cart-food";
import { CartItem } from "src/app/restaurants/models/cart-item";
import { Page } from "src/app/shared/models/page";
import { LoadCustomerOrderPayload } from "../../models/load-customer-order-payload";
import { LoadCustomerOrdersPayload } from "../../models/load-customer-orders-payload";
import { LoadDiscountCodePayload } from "../../models/load-discount-code-payload";

export const selectRestaurantAction = createAction('[Cart] Select Restaurant', props<{ payload: OrderRestaurant }>());
export const selectAddressAction = createAction('[Cart] Select Address', props<{ payload: Address }>());
export const addFoodToCartAction = createAction('[Cart] Add Food', props<{ payload: CartFood }>());
export const removeFoodFromCartAction = createAction('[Cart] Remove Food', props<{ payload: CartItem }>());
export const loadCartAction = createAction('[Cart] Load Cart', props<{ payload: CartItem[] }>());
export const increaseFoodQuantityAction = createAction('[Cart] Increase Food Quantity', props<{ payload: CartFood }>());
export const decreaseFoodQuantityAction = createAction('[Cart] Decrease Food Quantity', props<{ payload: CartFood }>());
export const clearCartAction = createAction('[Cart] Clear');

export const loadDiscountCodeAction = createAction('[Cart] Load Discount Code', props<{ payload: LoadDiscountCodePayload }>());
export const loadDiscountCodeSuccessAction = createAction('[Cart] Load Discount Code Success', props<{ payload: DiscountCode }>());
export const loadDiscountCodeErrorAction = createAction('[Cart] Load Discount Code Error', props<{ payload: any }>());

export const submitOrderAction = createAction('[Cart] Submit Order', props<{ payload: OrderCreate }>());
export const submitOrderSuccessAction = createAction('[Cart] Submit Order Success', props<{ payload: Order }>());
export const submitOrderErrorAction = createAction('[Cart] Submit Order Error', props<{ payload: any }>());
