import { Component, OnDestroy, OnInit } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { Store } from '@ngrx/store';
import { Observable, Subject } from 'rxjs';
import { startWith, switchMap } from 'rxjs/operators';
import { Order } from 'src/app/customers/models/order';
import { Page } from 'src/app/shared/models/page';
import { loadRestaurantOrdersAction } from 'src/app/store/restaurants/restaurants.actions';
import { selectRestaurantOrdersByPage } from 'src/app/store/restaurants/restaurants.selectors';

@Component({
  selector: 'app-restaurant-orders',
  templateUrl: './restaurant-orders.component.html',
  styleUrls: ['./restaurant-orders.component.scss']
})
export class RestaurantOrdersComponent implements OnInit, OnDestroy {
  private pageSelects$ = new Subject<number>();
  pagedOrders$: Observable<Page<Order>>;

  constructor(
    private store: Store,
  ) { }

  ngOnInit(): void {
    this.pageSelects$.pipe(
      startWith(0)
    ).subscribe(pageNumber => this.store.dispatch(loadRestaurantOrdersAction({ payload: pageNumber })));

    this.pagedOrders$ = this.pageSelects$.pipe(
      startWith(0),
      switchMap(pageNumber => this.store.select(selectRestaurantOrdersByPage(pageNumber)))
    );
  }

  ngOnDestroy(): void {
    this.pageSelects$.complete();
  }

  onPageChange(event: PageEvent) {
    this.pageSelects$.next(event.pageIndex);
  }
}
