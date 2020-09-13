import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { CartItem } from 'src/app/restaurants/models/cart-item';
import { Restaurant } from 'src/app/restaurants/models/restaurant';
import { CartService } from 'src/app/shared/cart.service';

@Component({
  selector: 'app-cart-items',
  templateUrl: './cart-items.component.html',
  styleUrls: ['./cart-items.component.scss']
})
export class CartItemsComponent implements OnInit {
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
