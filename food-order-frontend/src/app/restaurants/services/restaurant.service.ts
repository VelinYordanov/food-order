import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Restaurant } from '../models/restaurant';

@Injectable({
  providedIn: 'root'
})
export class RestaurantService {
  private BASE_URL: string = 'api/restaurants';
  constructor(private httpClient: HttpClient) { }

  getRestaurantData(restaurantId: string) {
    return this.httpClient.get<Restaurant>(`${this.BASE_URL}/${restaurantId}`);
  }
}
