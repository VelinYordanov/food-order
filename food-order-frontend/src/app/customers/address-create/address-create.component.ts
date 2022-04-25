import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { EMPTY } from 'rxjs';
import { catchError, first, switchMap, tap } from 'rxjs/operators';
import { User } from 'src/app/shared/models/user';
import { AlertService } from 'src/app/shared/services/alert.service';
import { loggedInUserSelector } from 'src/app/shared/store/authentication/authentication.selectors';
import { Address } from '../models/address';
import { CustomerService } from '../services/customer.service';
import { createAddressAction } from '../store/addresses/addresses.actions';

@Component({
  selector: 'app-address-create',
  templateUrl: './address-create.component.html',
  styleUrls: ['./address-create.component.scss']
})
export class AddressCreateComponent implements OnInit {
  constructor(
    private store: Store) { }

  ngOnInit(): void {
  }

  saveAddress(address: Address) {
    this.store.select(loggedInUserSelector)
      .pipe(
        first(x => !!x),
        tap((user: User) => this.store.dispatch(createAddressAction({ payload: { customerId: user.id, address: address } }))
        )).subscribe();
  }
}
