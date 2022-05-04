import { createAction, props } from "@ngrx/store";
import { OrderStatus } from "src/app/customers/models/order-status";
import { LoadCustomerOrderPayload } from "src/app/store/models/load-customer-order-payload";

export const activateAction = createAction('[Notifications] Activate');
export const deactivateAction = createAction('[Notifications] Deactivate');

export const subscribeToOrderUpdatesAction = createAction('[Notifications] Subscribe To Order Updates', props<{ payload: LoadCustomerOrderPayload }>());
export const unsubscribeFromOrderUpdatesAction = createAction('[Notifications] Unsubscribe From Order Updates');
export const orderUpdateAction = createAction('[Notifications] Order Update', props<{ payload: OrderStatus }>());