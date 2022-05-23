import { createAction, props } from "@ngrx/store";
import { Order } from "src/app/customers/models/order";
import { OrderStatus } from "src/app/customers/models/order-status";
import { Category } from "src/app/restaurants/models/category";
import { Food } from "src/app/restaurants/models/food";
import { Restaurant } from "src/app/restaurants/models/restaurant";
import { RestaurantEdit } from "src/app/restaurants/models/restaurant-edit";
import { RestaurantListItem } from "src/app/restaurants/models/restaurant-list-item";
import { Page } from "src/app/shared/models/page";
import { AddCategoryPayload } from "../models/add-category-payload";
import { AddFoodPayload } from "../models/add-food-payload";
import { OrderStatusChangePayload } from "../models/order-status-change-payload";
import { PromptError } from "../models/prompt-error";
import { PrompPayload } from "../models/prompt-payload";
import { PromptSuccess } from "../models/prompt-success";

export const loadRestaurantsAction = createAction('[Restaurants] Load Restaurants');
export const loadRestaurantsSuccessAction = createAction('[Restaurants] Load Restaurants Success', props<{ payload: RestaurantListItem[] }>());
export const loadRestaurantsErrorAction = createAction('[Restaurants] Load Restaurants Error', props<{ payload: any }>());

export const loadRestaurantAction = createAction('[Restaurants] Load Restaurant', props<{ payload: string }>());
export const loadRestaurantSuccessAction = createAction('[Restaurants] Load Restaurant Success', props<{ payload: Restaurant }>());
export const loadRestaurantErrorAction = createAction('[Restaurants] Load Restaurant Error', props<{ payload: any }>());

export const editRestaurantAction = createAction('[Restaurants] Edit Restaurant', props<{ payload: RestaurantEdit }>());
export const editRestaurantSuccessAction = createAction('[Restaurants] Edit Restaurant Success', props<{ payload: Restaurant }>());
export const editRestaurantErrorAction = createAction('[Restaurants] Edit Restaurant Error', props<{ payload: any }>());

export const addCategoryToRestaurantPromptAction = createAction('[Restaurants] Add Category To Restaurant Prompt', props<{ payload: PrompPayload<AddCategoryPayload> }>());
export const addCategoryToRestaurantAction = createAction('[Restaurants] Add Category To Restaurant', props<{ payload: PrompPayload<AddCategoryPayload> }>());
export const addCategoryToRestaurantSuccessAction = createAction('[Restaurants] Add Category To Restaurant Success', props<{ payload: PromptSuccess<Category> }>());
export const addCategoryToRestaurantErrorAction = createAction('[Restaurants] Add Category To Restaurant Error', props<{ payload: PromptError }>());

export const deleteCategoryFromRestaurantPromptAction = createAction('[Restaurants] Delete Category From Restaurant Prompt', props<{ payload: Category }>());
export const deleteCategoryFromRestaurantAction = createAction('[Restaurants] Delete Category From Restaurant', props<{ payload: Category }>());
export const deleteCategoryFromRestaurantSuccessAction = createAction('[Restaurants] Delete Category From Restaurant Success', props<{ payload: Category }>());
export const deleteCategoryFromRestaurantErrorAction = createAction('[Restaurants] Delete Category From Restaurant Error', props<{ payload: Category }>());

export const addFoodToRestaurantAction = createAction('[Restaurants] Add Food To Restaurant', props<{ payload: AddFoodPayload }>());
export const addFoodToRestaurantSuccessAction = createAction('[Restaurants] Add Food To Restaurant Success', props<{ payload: Food }>());
export const addFoodToRestaurantErrorAction = createAction('[Restaurants] Add Food To Restaurant Error', props<{ payload: any }>());

export const editRestaurantFoodAction = createAction('[Restaurants] Edit Restaurant Food', props<{ payload: Food }>());
export const editRestaurantFoodSuccessAction = createAction('[Restaurants] Edit Restaurant Food Success', props<{ payload: Food }>());
export const editRestaurantFoodErrorAction = createAction('[Restaurants] Edit Restaurant Food Error', props<{ payload: any }>());

export const deleteFoodFromRestaurantPromptAction = createAction('[Restaurants] Delete Food From Restaurant Prompt', props<{ payload: PrompPayload<string> }>());
export const deleteFoodFromRestaurantAction = createAction('[Restaurants] Delete Food From Restaurant', props<{ payload: PrompPayload<string> }>());
export const deleteFoodFromRestaurantSuccessAction = createAction('[Restaurants] Delete Food From Restaurant Success', props<{ payload: PromptSuccess<string> }>());
export const deleteFoodFromRestaurantErrorAction = createAction('[Restaurants] Delete Food From Restaurant Error', props<{ payload: PromptError }>());

export const loadRestaurantOrdersAction = createAction('[Restaurants] Load Restaurant Orders', props<{ payload: number }>());
export const loadRestaurantOrdersSuccessAction = createAction('[Restaurants] Load Restaurant Orders Success', props<{ payload: Page<Order> }>());
export const loadRestaurantOrdersErrorAction = createAction('[Restaurants] Load Restaurant Orders Error', props<{ payload: any }>());

export const updateRestaurantOrderAction = createAction('[Restaurants] Update Restaurant Order', props<{ payload: OrderStatusChangePayload }>());
export const updateRestaurantOrderSuccessAction = createAction('[Restaurants] Update Restaurant Order Sucess', props<{ payload: OrderStatusChangePayload }>());
export const updateRestaurantOrderErrorAction = createAction('[Restaurants] Update Restaurant Order Error', props<{ payload: any }>());
