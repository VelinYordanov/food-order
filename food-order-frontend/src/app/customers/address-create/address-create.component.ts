import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { first, tap } from 'rxjs/operators';
import { User } from 'src/app/shared/models/user';
import { loggedInUserSelector } from 'src/app/store/authentication/authentication.selectors';
import { Address } from '../models/address';
import { createAddressAction } from '../../store/customers/addresses/addresses.actions';

@Component({
  selector: 'app-address-create',
  templateUrl: './address-create.component.html',
  styleUrls: ['./address-create.component.scss']
})
export class AddressCreateComponent implements OnInit {
  constructor(private store: Store) { }

  ngOnInit(): void {
  }

  saveAddress(address: Address) {
    this.store.select(loggedInUserSelector)
      .pipe(
        first(x => !!x),
      ).subscribe(user => {
        const payload = {
          customerId: user.id,
          address
        }

        this.store.dispatch(createAddressAction({ payload }));
      });
  }
}
