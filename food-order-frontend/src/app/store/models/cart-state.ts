import { Address } from "src/app/customers/models/address";
import { DiscountCode } from "src/app/customers/models/discount-code";
import { OrderRestaurant } from "src/app/customers/models/order-restaurant";
import { CartItem } from "src/app/restaurants/models/cart-item";

export interface CartState {
    selectedRestaurant: OrderRestaurant,
    selectedAddress: Address,
    selectedDiscountCode: DiscountCode,
    selectedItems: CartItem[]
}