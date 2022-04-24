import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { EMPTY, Observable } from 'rxjs';
import { catchError, first, switchMap, tap } from 'rxjs/operators';
import { AlertService } from 'src/app/shared/services/alert.service';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
import { Address } from '../models/address';
import { CustomerService } from '../services/customer.service';
import { loadAddressesAction } from '../store/customers.actions';

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
    this.store.pipe(tap(something => console.log(something))).subscribe();
    this.addresses$ = this.store.select(state => state['addresses']);
    this.authenticationService.user$
      .pipe(
        first(x => !!x),
        tap(user => this.store.dispatch(loadAddressesAction({customerId: user.id})))
        // switchMap(user => this.customerService.getCustomerAddresses(user.id)
        //   .pipe(
        //     catchError(error => {
        //       this.alertService.displayMessage(error?.error?.description || 'An error occurred while loading addresses. Try again later.', 'error');
        //       return EMPTY;
        //     })
        //   ))
      ).subscribe();
  }
}
