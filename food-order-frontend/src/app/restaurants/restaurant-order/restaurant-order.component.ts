import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Subject } from 'rxjs';
import { Address } from 'src/app/customers/models/address';
import { Order } from 'src/app/customers/models/order';
import { OrderFoodResponse } from 'src/app/customers/models/order-food-response';
import { Status } from 'src/app/customers/models/status';
import { UtilService } from 'src/app/shared/services/util.service';
import { updateRestaurantOrderAction } from 'src/app/store/restaurants/restaurants.actions';

@Component({
  selector: 'app-restaurant-order',
  templateUrl: './restaurant-order.component.html',
  styleUrls: ['./restaurant-order.component.scss'],
})
export class RestaurantOrderComponent implements OnInit, OnDestroy {
  @Input() order: Order;

  showStatusButtons: boolean = false;
  Status = Status;

  private statusChanges$ = new Subject<Status>();

  constructor(
    private store: Store,
    private utilService: UtilService,
  ) { }

  ngOnInit(): void {
    this.statusChanges$.subscribe(statusChange =>
      this.store.dispatch(updateRestaurantOrderAction({ payload: { orderId: this.order.id, orderStatus: { status: statusChange } } })))
  }

  ngOnDestroy(): void {
    this.statusChanges$.complete();
  }

  calculateTotal(foods: OrderFoodResponse[]) {
    return this.utilService.calculateTotal(foods);
  }

  calculateTotalWithDiscount(
    foods: OrderFoodResponse[],
    discountPercentage: number
  ) {
    return this.utilService.calculateTotalWithDiscount(
      foods,
      discountPercentage
    );
  }

  getAddressData(address: Address) {
    return this.utilService.getAddressData(address);
  }

  toggleStatusButtons() {
    this.showStatusButtons = !this.showStatusButtons;
  }

  changeStatus(status: Status) {
    this.statusChanges$.next(status);
    this.toggleStatusButtons();
  }
}
