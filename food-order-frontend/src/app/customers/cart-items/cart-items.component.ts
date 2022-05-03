import { Component, Input, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { DiscountCode } from 'src/app/customers/models/discount-code';
import { OrderRestaurant } from 'src/app/customers/models/order-restaurant';
import { CartItem } from 'src/app/restaurants/models/cart-item';
import { increaseFoodQuantityAction, decreaseFoodQuantityAction, removeFoodFromCartAction } from 'src/app/store/customers/cart/cart.actions';
import { selectedItemsSelector, selectedRestaurantSelector } from 'src/app/store/customers/cart/cart.selectors';

@Component({
  selector: 'app-cart-items',
  templateUrl: './cart-items.component.html',
  styleUrls: ['./cart-items.component.scss']
})
export class CartItemsComponent implements OnInit {
  @Input('discountCode') discountCode: DiscountCode;

  selectedRestaurant$: Observable<OrderRestaurant>;
  items$: Observable<CartItem[]>;

  constructor(private store: Store) { }

  ngOnInit(): void {
    this.items$ = this.store.select(selectedItemsSelector);
    this.selectedRestaurant$ = this.store.select(selectedRestaurantSelector);
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
    this.store.dispatch(increaseFoodQuantityAction({ payload: item.food }));
  }

  decreaseQuantity(item: CartItem) {
    this.store.dispatch(decreaseFoodQuantityAction({ payload: item.food }));
  }

  removeFromCart(item: CartItem) {
    this.store.dispatch(removeFoodFromCartAction({ payload: item }));
  }
}
