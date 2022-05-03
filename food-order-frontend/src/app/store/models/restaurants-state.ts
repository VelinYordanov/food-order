import { Restaurant } from "src/app/restaurants/models/restaurant";
import { RestaurantListItem } from "src/app/restaurants/models/restaurant-list-item";

export interface RestaurantsState {
    restaurants: RestaurantListItem[],
    currentRestaurant: Restaurant
}