import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { first, tap } from 'rxjs/operators';
import { Address } from 'src/app/customers/models/address';
import { CartService } from 'src/app/shared/services/cart.service';
import { UtilService } from 'src/app/shared/services/util.service';
import { loggedInUserSelector } from 'src/app/store/authentication/authentication.selectors';
import { selectAddressAction } from 'src/app/store/customers/cart/cart.actions';
import { loadAddressesAction } from '../../store/customers/addresses/addresses.actions';
import { selectAddresses } from '../../store/customers/addresses/addresses.selectors';

@Component({
  selector: 'app-address-select',
  templateUrl: './address-select.component.html',
  styleUrls: ['./address-select.component.scss'],
})
export class AddressSelectComponent implements OnInit {
  addressForm: FormGroup;
  addresses$: Observable<Address[]>;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private formBuilder: FormBuilder,
    private utilService: UtilService,
    private store: Store,
  ) { }

  ngOnInit(): void {
    this.addressForm = this.formBuilder.group({
      address: [null, Validators.required],
    });

    this.store.select(loggedInUserSelector)
      .pipe(
        first()
      ).subscribe(user => this.store.dispatch(loadAddressesAction({ payload: user.id })));

    this.addresses$ = this.store.select(selectAddresses)
    .pipe(
      tap(addresses => addresses.length && this.addressForm.get('address').patchValue(addresses[0]))
    );
  }

  getAddressData(address: Address) {
    return this.utilService.getAddressData(address);
  }

  confirmAddress() {
    this.store.dispatch(selectAddressAction({ payload: this.addressForm.get('address').value }));

    this.router.navigate(['../', 'checkout'], {
      relativeTo: this.activatedRoute,
    });
  }

  compareAddresses(address1: Address, address2: Address) {
    return address1?.id === address2?.id;
  }
}
