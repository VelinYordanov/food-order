import { Component, OnDestroy, OnInit } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { EMPTY, Observable, Subject } from 'rxjs';
import { catchError, startWith, switchMap, withLatestFrom } from 'rxjs/operators';
import { Address } from 'src/app/customers/models/address';
import { Order } from 'src/app/customers/models/order';
import { OrderFoodResponse } from 'src/app/customers/models/order-food-response';
import { Page } from 'src/app/shared/models/page';
import { AlertService } from 'src/app/shared/services/alert.service';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
import { UtilService } from 'src/app/shared/services/util.service';
import { RestaurantService } from '../services/restaurant.service';

@Component({
  selector: 'app-restaurant-orders',
  templateUrl: './restaurant-orders.component.html',
  styleUrls: ['./restaurant-orders.component.scss']
})
export class RestaurantOrdersComponent implements OnInit, OnDestroy {
  private pageSelects$ = new Subject<number>();
  pagedOrders$: Observable<Page<Order>>;
  
  constructor(
    private authenticationService: AuthenticationService,
    private restaurantService: RestaurantService,
    private alertService: AlertService,
  ) { }

  ngOnInit(): void {
    this.pagedOrders$ = this.pageSelects$.pipe(
      startWith(0),
      withLatestFrom(this.authenticationService.user$),
      switchMap(([page, customer]) =>
        this.restaurantService.getOrders(customer.id, page).pipe(
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
