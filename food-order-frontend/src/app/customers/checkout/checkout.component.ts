import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, EMPTY, Observable, Subject } from 'rxjs';
import {
  catchError,
  filter,
  finalize,
  first,
  map,
  switchMap,
  switchMapTo,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { Address } from 'src/app/customers/models/address';
import { DiscountCode } from 'src/app/customers/models/discount-code';
import { CustomerService } from 'src/app/customers/services/customer.service';
import { AlertService } from 'src/app/shared/services/alert.service';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
import { CartService } from 'src/app/shared/services/cart.service';
import { UtilService } from 'src/app/shared/services/util.service';

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.scss'],
})
export class CheckoutComponent implements OnInit, OnDestroy {
  comment: FormControl;
  discountCodeFormControl: FormControl;

  selectedAddress$: Observable<Address>;

  numberOfFoods$: Observable<number>;

  discountCode: DiscountCode = null;

  private submitButtonClicks: Subject<void> = new Subject<void>();
  private applyDiscountCodeClicks: Subject<void> = new Subject<void>();

  constructor(
    private alertService: AlertService,
    private authenticationService: AuthenticationService,
    private customerService: CustomerService,
    private cartService: CartService,
    private utilService: UtilService,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.setUpSubmitOrder();
    this.setUpDiscountCodeLookup();

    this.numberOfFoods$ = this.cartService.selectedItems$.pipe(
      map((items) =>
        items
          .map((item) => item.quantity)
          .reduce((total, current) => total + current, 0),
      )
    );

    this.selectedAddress$ = this.cartService.selectedAddress$;

    this.discountCodeFormControl = new FormControl(null);
    this.comment = new FormControl(null);
  }

  ngOnDestroy(): void {
    this.submitButtonClicks.complete();
    this.applyDiscountCodeClicks.complete();
  }

  submitOrder() {
    this.submitButtonClicks.next();
  }

  getAddressData(address: Address) {
    return this.utilService.getAddressData(address);
  }

  applyDiscountCode() {
    this.applyDiscountCodeClicks.next();
  }

  private setUpDiscountCodeLookup() {
    this.applyDiscountCodeClicks
      .pipe(
        map((_) => this.discountCodeFormControl.value),
        filter((code) => !!code),
        withLatestFrom(this.cartService.selectedRestaurant$),
        switchMap(([code, restaurant]) =>
          this.customerService.getDiscountCode(restaurant.id, code).pipe(
            catchError((error) => {
              this.alertService.displayMessage(
                error?.error?.description ||
                  'An error occurred while looking up discount code. Try again later.',
                'error'
              );
              return EMPTY;
            })
          )
        )
      )
      .subscribe((discountCode) => (this.discountCode = discountCode));
  }

  private setUpSubmitOrder() {
    this.submitButtonClicks
      .pipe(
        switchMapTo(
          combineLatest([
            this.authenticationService.user$.pipe(map((user) => user.id)),
            this.cartService.selectedRestaurant$.pipe(
              map((restaurant) => restaurant.id)
            ),
            this.cartService.selectedAddress$.pipe(
              map((address) => address.id)
            ),
            this.cartService.selectedItems$.pipe(
              map((items) =>
                items.map((item) => ({
                  id: item.food.id,
                  quantity: item.quantity,
                }))
              )
            ),
          ]).pipe(
            first(),
            switchMap(([customerId, restaurantId, addressId, foods]) =>
              this.customerService
                .submitOrder({
                  restaurantId,
                  customerId,
                  addressId,
                  foods,
                  discountCodeId: this.discountCode?.id,
                  comment: this.comment.value,
                })
                .pipe(
                  catchError((error) => {
                    this.alertService.displayMessage(
                      error?.error?.description ||
                        'An error occurred while submitting order. Try again later',
                      'error'
                    );
                    return EMPTY;
                  })
                )
            ),
            finalize(() => this.cartService.clearCart())
          )
        )
      )
      .subscribe((order) => {
        this.alertService.displayMessage(
          'Successfully submitted order.',
          'success'
        );
        this.router.navigate(['../', order.id], {
          relativeTo: this.activatedRoute,
        });
      });
  }
}
