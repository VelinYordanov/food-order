import { Component, OnDestroy, OnInit } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { takeUntil, tap, withLatestFrom } from 'rxjs/operators';
import { Address } from '../models/address';
import { Store } from '@ngrx/store';
import { loadCustomerAddressAction, updateAddressAction } from '../../store/customers/addresses/addresses.actions';
import { loggedInUserWithRouteParameter } from 'src/app/store/authentication/authentication.selectors';
import { selectAddressById } from '../../store/customers/addresses/addresses.selectors';

@Component({
  selector: 'app-address-update',
  templateUrl: './address-update.component.html',
  styleUrls: ['./address-update.component.scss']
})
export class AddressUpdateComponent implements OnInit, OnDestroy {
  private readonly onDestoy$ = new Subject<void>();
  private readonly updateClicks$ = new Subject<Address>();

  address$: Observable<Address>;

  constructor(
    private store: Store
  ) { }

  ngOnInit(): void {
    this.address$ = this.store.select(selectAddressById);

    const userWithIdRouteParam$ = this.store.select(loggedInUserWithRouteParameter('id'))
      .pipe(
        takeUntil(this.onDestoy$),
      );

    userWithIdRouteParam$.subscribe(data => {
      const payload = {
        userId: data.userId,
        addressId: data.param
      };

      this.store.dispatch(loadCustomerAddressAction({ payload }));
    });

    this.updateClicks$
      .pipe(
        withLatestFrom(userWithIdRouteParam$),
      ).subscribe(([addressUpdateData, routerData]) => {
        const updateAddressPayload = {
          userId: routerData.userId,
          addressId: routerData.param,
          address: addressUpdateData
        };

        this.store.dispatch(updateAddressAction({ payload: updateAddressPayload }));
      })
  }

  updateAddress(address: Address) {
    this.updateClicks$.next(address);
  }

  ngOnDestroy(): void {
    this.onDestoy$.next();
    this.onDestoy$.complete();
    this.updateClicks$.complete();
  }
}
