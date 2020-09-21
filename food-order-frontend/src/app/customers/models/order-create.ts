import { OrderFood } from "./order-food";

export interface OrderCreate {
    restaurantId: string;
    customerId: string;
    addressId: string;
    discountCodeId: string;
    comment: string;
    foods: OrderFood[];
}