import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CartService } from 'src/app/shared/services/cart.service';
import { UtilService } from 'src/app/shared/services/util.service';
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
    private cartService: CartService,
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
    this.cartService.loadCart(cartItems);
    this.cartService.setRestaurant(order.restaurant);
    this.router.navigate(['restaurants', order.restaurant.id]);
  }

  trackOrder(orderId: string) {
    this.router.navigate(['order', orderId]);
  }
}