import { Component, Input, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { DiscountCode } from 'src/app/customers/models/discount-code';
import { CartItem } from 'src/app/restaurants/models/cart-item';
import { Restaurant } from 'src/app/restaurants/models/restaurant';
import { CartService } from 'src/app/shared/cart.service';

@Component({
  selector: 'app-cart-items',
  templateUrl: './cart-items.component.html',
  styleUrls: ['./cart-items.component.scss']
})
export class CartItemsComponent implements OnInit {
  @Input('discountCode') discountCode: DiscountCode;

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

  calculateTotalPriceWithDiscountCode(items: CartItem[]) {
    return this.calculateTotalPrice(items) * (1 - (this.discountCode.discountPercentage / 100));
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
