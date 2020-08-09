import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { CartItem } from '../restaurants/models/cart-item';
import { Food } from '../restaurants/models/food';
import { Restaurant } from '../restaurants/models/restaurant';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private currentRestaurant = new BehaviorSubject<Restaurant>(null);
  private foodCart = new BehaviorSubject<CartItem[]>([]);

  constructor() { }

  updateSelectedRestaurant(restaurant: Restaurant) {
    this.currentRestaurant.next(restaurant);
  }

  addItemToCart(food: Food) {
    const items = this.foodCart.getValue();
    const item = items.find(item => item.food.id === food.id);
    if (item) {
      item.quantity += 1;
    } else {
      items.push({ food, quantity: 1 });
    }

    this.foodCart.next(items);
  }

  increaseQuantity(food: Food) {
    const items = this.foodCart.getValue();
    const item = items.find(item => item.food.id === food.id);

    if (item) {
      item.quantity += 1;
    }

    this.foodCart.next(items);
  }

  decreaseQuantity(food: Food) {
    const items = this.foodCart.getValue();
    const item = items.find(item => item.food.id === food.id);

    if (item) {
      item.quantity -= 1;
      if (item.quantity === 0) {
        items.splice(items.findIndex(i => i === item), 1);
      }
    }

    this.foodCart.next(items);
  }

  removeFood(food: Food) {
    const items = this.foodCart.getValue();
    const index = items.findIndex(item => item.food.id === food.id);

    if (index !== -1) {
      items.splice(index, 1);
    }

    this.foodCart.next(items);
  }

  clearCart() {
    this.foodCart.next([]);
  }

  setRestaurant(restaurant: Restaurant) {
    if (restaurant !== this.currentRestaurant.getValue()) {
      this.foodCart.next([]);
      this.currentRestaurant.next(restaurant);
    }
  }
}
