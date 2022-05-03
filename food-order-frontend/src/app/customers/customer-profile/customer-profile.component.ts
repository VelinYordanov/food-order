import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { first, tap } from 'rxjs/operators';
import { loggedInUserSelector } from 'src/app/store/authentication/authentication.selectors';
import { Address } from '../models/address';
import { loadAddressesAction } from '../../store/customers/addresses/addresses.actions';
import { selectAddresses } from '../../store/customers/addresses/addresses.selectors';

@Component({
  selector: 'app-customer-profile',
  templateUrl: './customer-profile.component.html',
  styleUrls: ['./customer-profile.component.scss']
})
export class CustomerProfileComponent implements OnInit {
  addresses$: Observable<Address[]>;
  constructor(
    private store: Store) { }

  ngOnInit(): void {
    this.addresses$ = this.store.select(selectAddresses);
    this.store.select(loggedInUserSelector)
      .pipe(
        first(x => !!x),
      ).subscribe(user => this.store.dispatch(loadAddressesAction({ payload: user.id })));
  }
}
