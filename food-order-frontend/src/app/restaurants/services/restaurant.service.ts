import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Category } from '../models/category';
import { Food } from '../models/food';
import { Restaurant } from '../models/restaurant';
import { RestaurantEdit } from '../models/restaurant-edit';

@Injectable({
  providedIn: 'root'
})
export class RestaurantService {
  private readonly BASE_URL: string = 'api/restaurants';
  constructor(private httpClient: HttpClient) { }

  getRestaurantData(restaurantId: string) {
    return this.httpClient.get<Restaurant>(`${this.BASE_URL}/${restaurantId}`);
  }

  deleteCategoryFromRestaurant(restaurantId: string, categoryId: string) {
    return this.httpClient.delete(`${this.BASE_URL}/${restaurantId}/categories/${categoryId}`);
  }

  addCategoryToRestaurant(restaurantId: string, categoryName: string) {
    return this.httpClient.post<Category>(`${this.BASE_URL}/${restaurantId}/categories`, { name: categoryName });
  }

  addFood(restaurantId, food: Food) {
    return this.httpClient.post(`${this.BASE_URL}/${restaurantId}/foods`, food);
  }

  editRestaurant(restaurantId: string, restaurant: RestaurantEdit) {
    return this.httpClient.put(`${this.BASE_URL}/${restaurantId}`, restaurant);
  }

  editFood(restaurantId: string, foodId: string, food: Food) {
    return this.httpClient.put(`${this.BASE_URL}/${restaurantId}/foods/${foodId}`, food);
  }

  deleteFood(restaurantId: string, foodId: string) {
    return this.httpClient.delete(`${this.BASE_URL}/${restaurantId}/foods/${foodId}`);
  }
}
