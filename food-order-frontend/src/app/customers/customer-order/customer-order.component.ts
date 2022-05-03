import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { UtilService } from 'src/app/shared/services/util.service';
import { loadCartAction, selectRestaurantAction } from 'src/app/store/customers/cart/cart.actions';
import { Order } from '../models/order';
import { OrderFoodResponse } from '../models/order-food-response';
import { Status } from '../models/status';

@Component({
  selector: 'app-customer-order',
  templateUrl: './customer-order.component.html',
  styleUrls: ['./customer-order.component.scss']
})
export class CustomerOrderComponent implements OnInit {
  @Input() order: Order;

  constructor(
    private store: Store,
    private utilService: UtilService,
    private router: Router
  ) { }

  ngOnInit(): void {
  }

  calculateTotal(foods: OrderFoodResponse[]) {
    return this.utilService.calculateTotal(foods);
  }

  calculateTotalWithDiscount(foods: OrderFoodResponse[], discountPercentage: number) {
    return this.utilService.calculateTotalWithDiscount(foods, discountPercentage);
  }

  isTrackable(order: Order) {
    return order.status === Status.Accepted || order.status === Status.Pending;
  }

  isFinished(order: Order) {
    return (
      order.status === Status.Delivered || order.status === Status.Cancelled
    );
  }

  loadCart(order: Order) {
    const cartItems = order.foods.map((food) => ({ food, quantity: food.quantity }));
    this.store.dispatch(selectRestaurantAction({ payload: order.restaurant }));
    this.store.dispatch(loadCartAction({ payload: cartItems }));
    this.router.navigate(['restaurants', order.restaurant.id]);
  }

  trackOrder(orderId: string) {
    this.router.navigate(['customer', 'order', orderId]);
  }
}
