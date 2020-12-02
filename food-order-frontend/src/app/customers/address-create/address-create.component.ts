import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { EMPTY } from 'rxjs';
import { catchError, first, switchMap } from 'rxjs/operators';
import { AlertService } from 'src/app/shared/services/alert.service';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
import { Address } from '../models/address';
import { CustomerService } from '../services/customer.service';

@Component({
  selector: 'app-address-create',
  templateUrl: './address-create.component.html',
  styleUrls: ['./address-create.component.scss']
})
export class AddressCreateComponent implements OnInit {
  constructor(
    private router: Router,
    private alertService: AlertService,
    private authenticationService: AuthenticationService,
    private customerService: CustomerService) { }

  ngOnInit(): void {
  }

  saveAddress(address: Address) {
    this.authenticationService.user$
      .pipe(
        first(x => !!x),
        switchMap(user =>
          this.customerService.addAddressToCustomer(user.id, address)
            .pipe(
              catchError(error => {
                this.alertService.displayMessage(error?.error?.description || 'An error occurred while adding address. Try again later.', 'error');
                return EMPTY;
              })
            ))
      ).subscribe(address => {
        this.alertService.displayMessage("Successfully added address!", 'success');
        this.router.navigate(['customer-profile']);
      })
  }
}
