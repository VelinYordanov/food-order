import { Food } from "src/app/restaurants/models/food";

export interface AddFoodPayload {
    restaurantId: string,
    food: Food
}