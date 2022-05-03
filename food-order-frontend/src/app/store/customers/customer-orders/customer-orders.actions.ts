import { createAction, props } from "@ngrx/store";
import { Order } from "src/app/customers/models/order";
import { Page } from "src/app/shared/models/page";
import { LoadCustomerOrderPayload } from "../../models/load-customer-order-payload";
import { LoadCustomerOrdersPayload } from "../../models/load-customer-orders-payload";

export const loadCustomerOrdersAction = createAction('[Cart] Load Customer Orders', props<{ payload: LoadCustomerOrdersPayload }>());
export const loadCustomerOrdersSuccessAction = createAction('[Cart] Load Customer Orders Success', props<{ payload: Page<Order> }>());
export const loadCustomerOrdersErrorAction = createAction('[Cart] Load Customer Orders Error', props<{ payload: any }>());

export const loadOrderAction = createAction('[Cart] Load Customer Order', props<{ payload: LoadCustomerOrderPayload }>());
export const loadOrderSuccessAction = createAction('[Cart] Load Customer Order Success', props<{ payload: Order }>());
export const loadOrderErrorAction = createAction('[Cart] Load Customer Order Error', props<{ payload: any }>());