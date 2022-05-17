import { createAction, props } from "@ngrx/store";
import { Category } from "src/app/restaurants/models/category";
import { Food } from "src/app/restaurants/models/food";
import { Restaurant } from "src/app/restaurants/models/restaurant";
import { RestaurantListItem } from "src/app/restaurants/models/restaurant-list-item";
import { AddCategoryPayload } from "../models/add-category-payload";
import { AddFoodPayload } from "../models/add-food-payload";

export const loadRestaurantsAction = createAction('[Restaurants] Load Restaurants');
export const loadRestaurantsSuccessAction = createAction('[Restaurants] Load Restaurants Success', props<{ payload: RestaurantListItem[] }>());
export const loadRestaurantsErrorAction = createAction('[Restaurants] Load Restaurants Error', props<{ payload: any }>());

export const loadRestaurantAction = createAction('[Restaurants] Load Restaurant', props<{ payload: string }>());
export const loadRestaurantSuccessAction = createAction('[Restaurants] Load Restaurant Success', props<{ payload: Restaurant }>());
export const loadRestaurantErrorAction = createAction('[Restaurants] Load Restaurant Error', props<{ payload: any }>());

export const addCategoryToRestaurantPromptAction = createAction('[Restaurants] Add Category To Restaurant Prompt', props<{payload: AddCategoryPayload}>());
export const addCategoryToRestaurantAction = createAction('[Restaurants] Add Category To Restaurant', props<{payload: AddCategoryPayload}>());
export const addCategoryToRestaurantSuccessAction = createAction('[Restaurants] Add Category To Restaurant Success', props<{payload: Category}>());
export const addCategoryToRestaurantErrorAction = createAction('[Restaurants] Add Category To Restaurant Error', props<{payload: any}>());

export const addFoodToRestaurantAction = createAction('[Restaurants] Add Food To Restaurant', props<{payload: AddFoodPayload}>());
export const addFoodToRestaurantSuccessAction = createAction('[Restaurants] Add Food To Restaurant Success', props<{payload: Food}>());
export const addFoodToRestaurantErrorAction = createAction('[Restaurants] Add Food To Restaurant Error', props<{payload: any}>());