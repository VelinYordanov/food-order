import { createAction, props } from "@ngrx/store";
import { Restaurant } from "src/app/restaurants/models/restaurant";
import { RestaurantListItem } from "src/app/restaurants/models/restaurant-list-item";

export const loadRestaurantsAction = createAction('[Restaurants] Load Restaurants');
export const loadRestaurantsSuccessAction = createAction('[Restaurants] Load Restaurants Success', props<{ payload: RestaurantListItem[] }>());
export const loadRestaurantsErrorAction = createAction('[Restaurants] Load Restaurants Error', props<{ payload: any }>());

export const loadRestaurantAction = createAction('[Restaurants] Load Restaurant', props<{ payload: string }>());
export const loadRestaurantSuccessAction = createAction('[Restaurants] Load Restaurant Success', props<{ payload: Restaurant }>());
export const loadRestaurantErrorAction = createAction('[Restaurants] Load Restaurant Error', props<{ payload: any }>());