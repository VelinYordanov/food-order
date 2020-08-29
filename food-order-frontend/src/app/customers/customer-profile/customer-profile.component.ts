import { Component, OnInit } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, first, switchMap } from 'rxjs/operators';
import { AlertService } from 'src/app/shared/alert.service';
import { AuthenticationService } from 'src/app/shared/authentication.service';
import { Address } from '../models/address';
import { CustomerService } from '../services/customer.service';

@Component({
  selector: 'app-customer-profile',
  templateUrl: './customer-profile.component.html',
  styleUrls: ['./customer-profile.component.scss']
})
export class CustomerProfileComponent implements OnInit {
  addresses$: Observable<Address[]>;
  constructor(
    private alertService: AlertService,
    private authenticationService: AuthenticationService,
    private customerService: CustomerService) { }

  ngOnInit(): void {
    this.addresses$ = this.authenticationService.user$
      .pipe(
        first(x => !!x),
        switchMap(user => this.customerService.getCustomerAddresses(user.id)
          .pipe(
            catchError(error => {
              this.alertService.displayMessage(error?.error?.description || 'An error occurred while loading addresses. Try again later.', 'error');
              return throwError(error);
            })
          ))
      )
  }
}
