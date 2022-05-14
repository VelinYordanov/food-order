import { DiscountCodeItem } from "src/app/restaurants/models/discount-code-item";
import { Restaurant } from "src/app/restaurants/models/restaurant";
import { RestaurantListItem } from "src/app/restaurants/models/restaurant-list-item";
import { GraphState } from "./graph-state";

export interface RestaurantsState {
    restaurants: RestaurantListItem[],
    currentRestaurant: Restaurant,
    discountCodes: DiscountCodeItem[],
    graphData: GraphState
}