import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Actions, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { Observable, Subject } from 'rxjs';
import { filter, first, takeUntil, tap } from 'rxjs/operators';
import { Address } from 'src/app/customers/models/address';
import { CartService } from 'src/app/shared/services/cart.service';
import { UtilService } from 'src/app/shared/services/util.service';
import { loggedInUserSelector } from 'src/app/shared/store/authentication/authentication.selectors';
import { loadAddressesAction, loadAddressesSuccessAction } from '../store/addresses/addresses.actions';
import { selectAddresses } from '../store/addresses/addresses.selectors';

@Component({
  selector: 'app-address-select',
  templateUrl: './address-select.component.html',
  styleUrls: ['./address-select.component.scss'],
})
export class AddressSelectComponent
  implements OnInit, AfterViewInit, OnDestroy {
  addressForm: FormGroup;
  addresses$: Observable<Address[]>;

  private onDestroy$ = new Subject<void>();

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private formBuilder: FormBuilder,
    private cartService: CartService,
    private utilService: UtilService,
    private store: Store,
    private actions: Actions
  ) { }

  ngOnInit(): void {
    this.addressForm = this.formBuilder.group({
      address: [null, Validators.required],
    });

    this.store.select(loggedInUserSelector)
      .pipe(
        first(),
        tap(user => this.store.dispatch(loadAddressesAction({ payload: user.id })))
      );

    this.addresses$ = this.store.select(selectAddresses);

    this.actions
      .pipe(
        takeUntil(this.onDestroy$),
        ofType(loadAddressesSuccessAction),
        tap(({ payload }) => payload.length && this.addressForm.get('address').patchValue(payload[0]))
      );
  }

  ngAfterViewInit(): void {
    this.cartService.selectedAddress$
      .pipe(
        takeUntil(this.onDestroy$),
        filter(x => !!x),
      )
      .subscribe(
        (address) => this.addressForm.get('address').setValue(address)
      );
  }

  getAddressData(address: Address) {
    return this.utilService.getAddressData(address);
  }

  confirmAddress() {
    this.cartService.updateSelectedAddress(
      this.addressForm.get('address').value
    );
    this.router.navigate(['../', 'checkout'], {
      relativeTo: this.activatedRoute,
    });
  }

  compareAddresses(address1: Address, address2: Address) {
    return address1?.id === address2?.id;
  }

  ngOnDestroy(): void {
    this.onDestroy$.next();
    this.onDestroy$.complete();
  }
}
