import { createAction, props } from "@ngrx/store";
import { OrderStatus } from "src/app/customers/models/order-status";
import { Order } from "src/app/customers/models/order";
import { LoadCustomerOrderPayload } from "src/app/store/models/load-customer-order-payload";

export const activateAction = createAction('[Notifications] Activate');
export const deactivateAction = createAction('[Notifications] Deactivate');

export const subscribeToOrderUpdatesAction = createAction('[Notifications] Subscribe To Order Updates', props<{ payload: LoadCustomerOrderPayload }>());
export const unsubscribeFromOrderUpdatesAction = createAction('[Notifications] Unsubscribe From Order Updates');
export const orderUpdateAction = createAction('[Notifications] Order Update', props<{ payload: OrderStatus }>());

export const subscribeToRestaurantOrdersAction = createAction('[Notifications] Subscribe To Restaurant Orders', props<{ payload: string }>());
export const unsubscribeFromRestaurantOrdersAction = createAction('[Notifications] Unsubscribe From Restaurant Orders');
export const restaurantOrderUpdateAction = createAction('[Notifications] Restaurant Order Update', props<{ payload: Order }>());