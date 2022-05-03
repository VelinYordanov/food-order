import { Component, OnDestroy, OnInit } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { Actions, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { Observable, Subject } from 'rxjs';
import {
  map,
  startWith,
  takeUntil,
  withLatestFrom,
} from 'rxjs/operators';
import { Page } from 'src/app/shared/models/page';
import { loggedInUserIdSelector } from 'src/app/store/authentication/authentication.selectors';
import { loadCustomerOrdersAction } from 'src/app/store/customers/customer-orders/customer-orders.actions';
import { selectCustomerOrders } from 'src/app/store/customers/customer-orders/customer-orders.selectors';
import { Order } from '../models/order';

@Component({
  selector: 'app-customer-orders',
  templateUrl: './customer-orders.component.html',
  styleUrls: ['./customer-orders.component.scss'],
})
export class CustomerOrdersComponent implements OnInit, OnDestroy {
  private readonly pageSelects$ = new Subject<number>();
  private readonly onDestroy$ = new Subject<void>();

  pagedOrders$: Observable<Page<Order>>;

  constructor(
    private store: Store,
  ) { }

  ngOnInit(): void {
    this.pagedOrders$ = this.store.select(selectCustomerOrders);

    this.pageSelects$.pipe(
      takeUntil(this.onDestroy$),
      startWith(0),
      withLatestFrom(this.store.select(loggedInUserIdSelector)),
    ).subscribe(([page, customerId]) => this.store.dispatch(loadCustomerOrdersAction({ payload: { page, customerId } })))
  }

  ngOnDestroy(): void {
    this.onDestroy$.next();
    this.onDestroy$.complete();
    this.pageSelects$.complete();
  }

  onPageChange(event: PageEvent) {
    this.pageSelects$.next(event.pageIndex);
  }
}
