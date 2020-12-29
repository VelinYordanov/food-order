import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Category } from '../models/category';
import { Food } from '../models/food';
import { Restaurant } from '../models/restaurant';
import { RestaurantListItem } from '../models/restaurant-list-item';
import { RestaurantEdit } from '../models/restaurant-edit';
import { Page } from 'src/app/shared/models/page';
import { Order } from 'src/app/customers/models/order';
import { OrderStatus } from 'src/app/customers/models/order-status';
import { DiscountCodeDto } from '../models/discount-code-dto';

@Injectable({
  providedIn: 'root',
})
export class RestaurantService {
  private readonly BASE_URL: string = 'api/restaurants';
  constructor(private httpClient: HttpClient) {}

  getRestaurantsList() {
    return this.httpClient.get<RestaurantListItem[]>(`${this.BASE_URL}`);
  }

  getRestaurantData(restaurantId: string) {
    return this.httpClient.get<Restaurant>(`${this.BASE_URL}/${restaurantId}`);
  }

  deleteCategoryFromRestaurant(restaurantId: string, categoryId: string) {
    return this.httpClient.delete(
      `${this.BASE_URL}/${restaurantId}/categories/${categoryId}`
    );
  }

  addCategoryToRestaurant(restaurantId: string, categoryName: string) {
    return this.httpClient.post<Category>(
      `${this.BASE_URL}/${restaurantId}/categories`,
      { name: categoryName }
    );
  }

  addFood(restaurantId, food: Food) {
    return this.httpClient.post<Food>(
      `${this.BASE_URL}/${restaurantId}/foods`,
      food
    );
  }

  editRestaurant(restaurantId: string, restaurant: RestaurantEdit) {
    return this.httpClient.put<Restaurant>(
      `${this.BASE_URL}/${restaurantId}`,
      restaurant
    );
  }

  editFood(restaurantId: string, foodId: string, food: Food) {
    return this.httpClient.put<Food>(
      `${this.BASE_URL}/${restaurantId}/foods/${foodId}`,
      food
    );
  }

  deleteFood(restaurantId: string, foodId: string) {
    return this.httpClient.delete(
      `${this.BASE_URL}/${restaurantId}/foods/${foodId}`
    );
  }

  getOrders(restaurantId: string, page: number) {
    return this.httpClient.get<Page<Order>>(
      `${this.BASE_URL}/${restaurantId}/orders`,
      { params: { page: String(page) } }
    );
  }

  changeOrderStatus(
    restaurantId: string,
    orderId: string,
    status: OrderStatus
  ) {
    return this.httpClient.patch<OrderStatus>(
      `${this.BASE_URL}/${restaurantId}/orders/${orderId}`,
      status
    );
  }

  createDiscountCode(restaurantId: string, discountCode: DiscountCodeDto) {
    return this.httpClient.post(
      `${this.BASE_URL}/${restaurantId}/discount-codes`,
      discountCode
    );
  }
}
