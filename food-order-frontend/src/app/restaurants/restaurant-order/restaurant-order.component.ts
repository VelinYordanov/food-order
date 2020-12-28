import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { EMPTY, Subject } from 'rxjs';
import { catchError, switchMap, withLatestFrom } from 'rxjs/operators';
import { Address } from 'src/app/customers/models/address';
import { Order } from 'src/app/customers/models/order';
import { OrderFoodResponse } from 'src/app/customers/models/order-food-response';
import { Status } from 'src/app/customers/models/status';
import { AlertService } from 'src/app/shared/services/alert.service';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
import { UtilService } from 'src/app/shared/services/util.service';
import { RestaurantService } from '../services/restaurant.service';

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
    private utilService: UtilService,
    private authenticationService: AuthenticationService,
    private alertService: AlertService,
    private restaurantService: RestaurantService
  ) {}

  ngOnInit(): void {
    this.statusChanges$
      .pipe(
        withLatestFrom(this.authenticationService.user$),
        switchMap(([status, restaurant]) =>
          this.restaurantService
            .changeOrderStatus(restaurant.id, this.order.id, { status })
            .pipe(
              catchError((error) => {
                this.alertService.displayMessage(
                  error?.error?.description ||
                    'An error occurred while changing order status. Try again later.',
                  'error'
                );
                return EMPTY;
              })
            )
        )
      )
      .subscribe((orderStatus) => (this.order.status = orderStatus.status));
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
