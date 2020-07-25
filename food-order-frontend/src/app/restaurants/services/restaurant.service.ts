import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Category } from '../models/category';
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

  editRestaurant(restaurantId: string, restaurant: RestaurantEdit) {
    return this.httpClient.put(`${this.BASE_URL}/${restaurantId}`, restaurant);
  }
}
