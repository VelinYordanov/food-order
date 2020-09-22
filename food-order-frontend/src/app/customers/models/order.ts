import { Address } from './address';

import { OrderFoodResponse } from './order-food-response';
import { OrderRestaurant } from './order-restaurant';
import { OrderCustomer } from './order-customer';
import { DiscountCode } from './discount-code';

export interface Order {
    id: string;
    status: number;
    restaurant: OrderRestaurant;
    customer: OrderCustomer;
    address: Address;
    discountCode: DiscountCode;
    foods: OrderFoodResponse[];
    comment: string;
}