import { createReducer, on } from "@ngrx/store";
import { CustomerOrdersState } from "../../models/customer-orders-state";
import { loadCustomerOrdersSuccessAction, loadOrderErrorAction, loadOrderSuccessAction } from "./customer-orders.actions";

const initialState: CustomerOrdersState = {
    orders: null,
    currentOrder: null
}

export const customerOrdersReducer = createReducer(
    initialState,
    on(loadCustomerOrdersSuccessAction, (state, action) => ({ ...state, ...{ orders: action.payload } })),
    on(loadOrderSuccessAction, (state, action) => ({ ...state, ...{ currentOrder: action.payload } })),
    on(loadOrderErrorAction, (state, action) => ({ ...state, ...{ currentOrder: null } }))
)
