import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { Order } from 'src/app/customers/models/order';
import { EnumData } from 'src/app/shared/models/enum-data';
import { Store } from '@ngrx/store';
import { Actions, ofType } from '@ngrx/effects';
import { loggedInUserWithRouteParameter } from 'src/app/store/authentication/authentication.selectors';
import { loadOrderStatusesAction } from 'src/app/store/enums/enums.actions';
import { orderTypesSelector } from 'src/app/store/enums/enums.selectors';
import { activateAction, subscribeToOrderUpdatesAction, orderUpdateAction, deactivateAction, unsubscribeFromOrderUpdatesAction } from 'src/app/store/notifications/notification.actions';
import { loadOrderAction, loadOrderSuccessAction } from 'src/app/store/customers/customer-orders/customer-orders.actions';
import { selectCurrentCustomerOrder } from 'src/app/store/customers/customer-orders/customer-orders.selectors';

@Component({
  selector: 'app-successful-order',
  templateUrl: './successful-order.component.html',
  styleUrls: ['./successful-order.component.scss']
})
export class SuccessfulOrderComponent implements OnInit, OnDestroy {
  order: Order;

  private orderStatuses: EnumData[] = [];
  private onDestroy$ = new Subject<void>();

  constructor(
    private store: Store,
    private actions$: Actions) { }

  ngOnInit(): void {
    this.store.dispatch(activateAction());
    this.store.dispatch(loadOrderStatusesAction());

    this.store.select(orderTypesSelector).pipe(
      takeUntil(this.onDestroy$)
    ).subscribe(orderStasuses => this.orderStatuses = orderStasuses);

    this.store.select(selectCurrentCustomerOrder)
      .pipe(takeUntil(this.onDestroy$)
      ).subscribe(order => this.order = order);

    this.store.select(loggedInUserWithRouteParameter('id'))
      .pipe(
        takeUntil(this.onDestroy$)
      ).subscribe(data => {
        const payload = { customerId: data.userId, orderId: data.param };
        this.store.dispatch(loadOrderAction({ payload }));
        this.store.dispatch(subscribeToOrderUpdatesAction({ payload }));
      });

    this.actions$.pipe(
      takeUntil(this.onDestroy$),
      ofType(orderUpdateAction)
    ).subscribe(action => this.order.status = action.payload.status);
  }

  ngOnDestroy(): void {
    this.onDestroy$.next();
    this.onDestroy$.complete();
    this.store.dispatch(unsubscribeFromOrderUpdatesAction());
    this.store.dispatch(deactivateAction());
  }

  getOrderStatus(status: number): string {
    return this.orderStatuses?.find(orderStatus => orderStatus.id === status)?.value;
  }
}
