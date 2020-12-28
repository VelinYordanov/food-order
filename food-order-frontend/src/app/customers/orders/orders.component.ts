import { Component, OnDestroy, OnInit } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { Router } from '@angular/router';
import { EMPTY, Observable, Subject } from 'rxjs';
import {
  catchError,
  startWith,
  switchMap,
  withLatestFrom,
} from 'rxjs/operators';
import { Page } from 'src/app/shared/models/page';
import { AlertService } from 'src/app/shared/services/alert.service';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
import { CartService } from 'src/app/shared/services/cart.service';
import { Order } from '../models/order';
import { OrderFoodResponse } from '../models/order-food-response';
import { Status } from '../models/status';
import { CustomerService } from '../services/customer.service';

@Component({
  selector: 'app-orders',
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.scss'],
})
export class OrdersComponent implements OnInit, OnDestroy {
  private pageSelects$ = new Subject<number>();
  pagedOrders$: Observable<Page<Order>>;

  constructor(
    private authenticationService: AuthenticationService,
    private customerService: CustomerService,
    private alertService: AlertService,
    private cartService: CartService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.pagedOrders$ = this.pageSelects$.pipe(
      startWith(0),
      withLatestFrom(this.authenticationService.user$),
      switchMap(([page, customer]) =>
        this.customerService.getOrders(customer.id, page).pipe(
          catchError((error) => {
            this.alertService.displayMessage(
              error?.error?.description ||
                'An error occurred while loading orders. Try again later.',
              'error'
            );
            return EMPTY;
          })
        )
      )
    );
  }

  ngOnDestroy(): void {
    this.pageSelects$.complete();
  }

  calculateTotal(foods: OrderFoodResponse[]) {
    return foods.reduce((acc, curr) => acc + curr.price, 0);
  }

  calculateTotalWithDiscount(foods: OrderFoodResponse[], discountPercentage: number) {
    return this.calculateTotal(foods) * ((100 - discountPercentage) / 100);
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

  onPageChange(event: PageEvent) {
    this.pageSelects$.next(event.pageIndex);
  }
}
