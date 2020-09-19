import { Address } from './address';

import { OrderFoodResponse } from './order-food-response';
import { OrderRestaurant } from './order-restaurant';
import { OrderCustomer } from './order-customer';

export interface Order {
    id: string;
    restaurant: OrderRestaurant;
    customer: OrderCustomer;
    address: Address;
    foods: OrderFoodResponse[];
    comment: string;
}