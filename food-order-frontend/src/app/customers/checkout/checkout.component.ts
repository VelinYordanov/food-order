import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { Actions, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { EMPTY, Observable, Subject } from 'rxjs';
import {
  catchError,
  filter,
  map,
  switchMapTo,
  takeUntil,
  withLatestFrom,
} from 'rxjs/operators';
import { Address } from 'src/app/customers/models/address';
import { DiscountCode } from 'src/app/customers/models/discount-code';
import { UtilService } from 'src/app/shared/services/util.service';
import { loadDiscountCodeAction, loadDiscountCodeSuccessAction, submitOrderAction } from '../store/cart/cart.actions';
import { cartItemsSumSelector, orderItemsSelector, selectedAddressSelector, selectedRestaurantIdSelector } from '../store/cart/cart.selectors';

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

  private readonly submitButtonClicks: Subject<void> = new Subject<void>();
  private readonly applyDiscountCodeClicks: Subject<void> = new Subject<void>();
  private readonly onDestroy$ = new Subject<void>();

  constructor(
    private utilService: UtilService,
    private store: Store,
    private actions$: Actions
  ) { }

  ngOnInit(): void {
    this.setUpSubmitOrder();
    this.setUpDiscountCodeLookup();

    this.numberOfFoods$ = this.store.select(cartItemsSumSelector)
    this.selectedAddress$ = this.store.select(selectedAddressSelector);

    this.discountCodeFormControl = new FormControl(null);
    this.comment = new FormControl(null);
  }

  ngOnDestroy(): void {
    this.onDestroy$.next();
    this.onDestroy$.complete();
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
        withLatestFrom(this.store.select(selectedRestaurantIdSelector)),
      ).subscribe(([code, restaurantId]) => this.store.dispatch(loadDiscountCodeAction({ payload: { code, restaurantId } })));

    this.actions$.pipe(
      takeUntil(this.onDestroy$),
      ofType(loadDiscountCodeSuccessAction),
    ).subscribe(action => this.discountCode = action.payload)
  }

  private setUpSubmitOrder() {
    this.submitButtonClicks.pipe(
      switchMapTo(this.store.select(orderItemsSelector)
        .pipe(
          map(ordersData => ({ ...ordersData, ...{ discountCodeId: this.discountCode.id, comment: this.comment.value } })),
          catchError(error => EMPTY)
        ))
    ).subscribe(orderCreate => this.store.dispatch(submitOrderAction({ payload: orderCreate })));
  }
}
