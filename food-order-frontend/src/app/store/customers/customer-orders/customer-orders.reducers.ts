import { createReducer, on } from "@ngrx/store";
import { CustomerOrdersState } from "../../models/customer-orders-state";
import { orderUpdateAction } from "../../notifications/notification.actions";
import { loadCustomerOrdersSuccessAction, loadOrderErrorAction, loadOrderSuccessAction } from "./customer-orders.actions";

const initialState: CustomerOrdersState = {
    orders: null,
    currentOrder: null
}

export const customerOrdersReducer = createReducer(
    initialState,
    on(loadCustomerOrdersSuccessAction, (state, action) => ({ ...state, ...{ orders: action.payload } })),
    on(loadOrderSuccessAction, (state, action) => ({ ...state, ...{ currentOrder: action.payload } })),
    on(loadOrderErrorAction, (state, action) => ({ ...state, ...{ currentOrder: null } })),
    on(orderUpdateAction, (state, action) => ({ ...state, currentOrder: { ...state.currentOrder, status: action.payload.status } }))
)
