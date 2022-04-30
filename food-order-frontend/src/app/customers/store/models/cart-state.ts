import { CartItem } from "src/app/restaurants/models/cart-item";
import { Address } from "../../models/address";
import { OrderRestaurant } from "../../models/order-restaurant";

export interface CartState {
    selectedRestaurant: OrderRestaurant,
    selectedAddress: Address,
    selectedItems: CartItem[]
}