import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { CartItem } from 'src/app/restaurants/models/cart-item';
import { Restaurant } from 'src/app/restaurants/models/restaurant';
import { CartService } from '../../shared/cart.service';

@Component({
  selector: 'app-cart-component',
  templateUrl: './cart-component.component.html',
  styleUrls: ['./cart-component.component.scss']
})
export class CartComponentComponent implements OnInit {
  selectedRestaurant$: Observable<Restaurant>;
  items$: Observable<CartItem[]>;

  constructor(private cartService: CartService) { }

  ngOnInit(): void {
    this.items$ = this.cartService.selectedItems$;
    this.selectedRestaurant$ = this.cartService.selectedRestaurant$;
  }

  calculateItemPrice(item: CartItem) {
    return item.food.price * item.quantity;
  }

  calculateTotalPrice(items: CartItem[]) {
    return items.reduce((total, current) => {
      return total + this.calculateItemPrice(current);
    }, 0);
  }

  increaseQuantity(item: CartItem) {
    this.cartService.increaseQuantity(item.food);
  }

  decreaseQuantity(item: CartItem) {
    this.cartService.decreaseQuantity(item.food);
  }

  removeFromCart(item: CartItem) {
    this.cartService.removeFood(item.food);
  }
}
