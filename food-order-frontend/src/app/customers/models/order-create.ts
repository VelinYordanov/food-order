import { OrderFood } from "./order-food";

export interface OrderCreate {
    restaurantId: string;
    customerId: string;
    addressId: string;
    comment: string;
    foods: OrderFood[];
}