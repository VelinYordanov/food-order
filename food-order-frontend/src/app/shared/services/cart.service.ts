import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Address } from '../../customers/models/address';
import { CartItem } from '../../restaurants/models/cart-item';
import { CartFood } from '../../restaurants/models/cart-food';
import { StorageService } from './storage.service';
import { OrderRestaurant } from 'src/app/customers/models/order-restaurant';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private currentRestaurant = new BehaviorSubject<OrderRestaurant>(JSON.parse(this.storageService.getItem('restaurant')));
  private selectedAddress = new BehaviorSubject<Address>(JSON.parse(this.storageService.getItem('address')));
  private foodCart = new BehaviorSubject<CartItem[]>(JSON.parse(this.storageService.getItem("cart")));

  selectedRestaurant$: Observable<OrderRestaurant> = this.currentRestaurant.asObservable();
  selectedAddress$: Observable<Address> = this.selectedAddress.asObservable();
  selectedItems$: Observable<CartItem[]> = this.foodCart.asObservable();

  constructor(private storageService: StorageService) {
    this.selectedItems$.subscribe(items => this.storageService.setItem('cart', JSON.stringify(items)));
    this.currentRestaurant.subscribe(restaurant => this.storageService.setItem('restaurant', JSON.stringify(restaurant)));
    this.selectedAddress.subscribe(address => this.storageService.setItem('address', JSON.stringify(address)));
  }

  updateSelectedAddress(address: Address) {
    this.selectedAddress.next(address);
  }

  addItemToCart(food: CartFood) {
    const items = this.foodCart.getValue();
    const item = items.find((item) => item.food.id === food.id);
    if (item) {
      item.quantity += 1;
    } else {
      items.push({ food, quantity: 1 });
    }

    this.foodCart.next(items);
  }

  loadCart(items: CartItem[]) {
    const existingItems = this.foodCart.getValue();
    items.forEach(item => {
      const currentItem = existingItems.find(existingItem => existingItem.food.id === item.food.id);
      if(currentItem) {
        currentItem.quantity += item.quantity;
      } else {
        existingItems.push(item);
      }
    })

    this.foodCart.next(existingItems);
  }

  increaseQuantity(food: CartFood) {
    const items = this.foodCart.getValue();
    const item = items.find((item) => item.food.id === food.id);

    if (item) {
      item.quantity += 1;
    }

    this.foodCart.next(items);
  }

  decreaseQuantity(food: CartFood) {
    const items = this.foodCart.getValue();
    const item = items.find((item) => item.food.id === food.id);

    if (item) {
      item.quantity -= 1;
      if (item.quantity === 0) {
        items.splice(
          items.findIndex((i) => i === item),
          1
        );
      }
    }

    this.foodCart.next(items);
  }

  removeFood(food: CartFood) {
    const items = this.foodCart.getValue();
    const index = items.findIndex((item) => item.food.id === food.id);

    if (index !== -1) {
      items.splice(index, 1);
    }

    this.foodCart.next(items);
  }

  clearCart() {
    this.foodCart.next([]);
    this.currentRestaurant.next(null);
    this.selectedAddress.next(null);
  }

  setRestaurant(restaurant: OrderRestaurant) {
    if (restaurant.id !== this.currentRestaurant.getValue()?.id) {
      this.foodCart.next([]);
      this.currentRestaurant.next(restaurant);
    }
  }
}
