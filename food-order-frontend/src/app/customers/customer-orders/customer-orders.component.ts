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
import { UtilService } from 'src/app/shared/services/util.service';
import { Order } from '../models/order';
import { OrderFoodResponse } from '../models/order-food-response';
import { Status } from '../models/status';
import { CustomerService } from '../services/customer.service';

@Component({
  selector: 'app-customer-orders',
  templateUrl: './customer-orders.component.html',
  styleUrls: ['./customer-orders.component.scss'],
})
export class CutomerOrdersComponent implements OnInit, OnDestroy {
  private pageSelects$ = new Subject<number>();
  pagedOrders$: Observable<Page<Order>>;

  constructor(
    private authenticationService: AuthenticationService,
    private customerService: CustomerService,
    private alertService: AlertService,
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

  onPageChange(event: PageEvent) {
    this.pageSelects$.next(event.pageIndex);
  }
}
