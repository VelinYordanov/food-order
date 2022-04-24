import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { EMPTY, Observable } from 'rxjs';
import { catchError, first, switchMap, tap } from 'rxjs/operators';
import { AlertService } from 'src/app/shared/services/alert.service';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
import { Address } from '../models/address';
import { CustomerService } from '../services/customer.service';
import { loadAddressesAction } from '../store/addresses/addresses.actions';
import { selectAddresses } from '../store/addresses/addresses.selectors';

@Component({
  selector: 'app-customer-profile',
  templateUrl: './customer-profile.component.html',
  styleUrls: ['./customer-profile.component.scss']
})
export class CustomerProfileComponent implements OnInit {
  addresses$: Observable<Address[]>;
  constructor(
    private store: Store,
    private alertService: AlertService,
    private authenticationService: AuthenticationService,
    private customerService: CustomerService) { }

  ngOnInit(): void {
    this.addresses$ = this.store.select(selectAddresses);
    this.authenticationService.user$
      .pipe(
        first(x => !!x),
        tap(user => this.store.dispatch(loadAddressesAction({customerId: user.id})))
      ).subscribe();
  }
}
