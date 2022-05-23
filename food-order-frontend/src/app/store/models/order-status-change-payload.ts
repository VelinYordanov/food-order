import { OrderStatus } from "src/app/customers/models/order-status"

export interface OrderStatusChangePayload {
    orderId: string
    orderStatus: OrderStatus
}