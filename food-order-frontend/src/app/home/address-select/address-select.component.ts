import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Observable, throwError } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { Address } from 'src/app/customers/models/address';
import { CustomerService } from 'src/app/customers/services/customer.service';
import { AlertService } from 'src/app/shared/alert.service';
import { AuthenticationService } from 'src/app/shared/authentication.service';
import { CartService } from 'src/app/shared/cart.service';

@Component({
  selector: 'app-address-select',
  templateUrl: './address-select.component.html',
  styleUrls: ['./address-select.component.scss']
})
export class AddressSelectComponent implements OnInit {
  addressForm: FormGroup;
  addresses$: Observable<Address[]>;

  constructor(
    private formBuilder: FormBuilder,
    private cartService:CartService,
    private authenticationService: AuthenticationService,
    private alertService: AlertService,
    private customerService: CustomerService) { }

  ngOnInit(): void {
    this.addressForm = this.formBuilder.group({
      address: [null, Validators.required]
    });

    this.addresses$ = this.authenticationService.user$
      .pipe(
        switchMap(user =>
          this.customerService.getCustomerAddresses(user.id)
            .pipe(
              catchError(error => {
                this.alertService.displayMessage(error?.error?.description || "An error occurred while loading addresses. Try again later.", 'error');
                return throwError(error);
              })
            ))
      )
  }

  getAddressData(address: Address) {
    return [address.neighborhood, address.street, address.streetNumber, address.apartmentBuildingNumber]
      .filter(Boolean)
      .join(", ");
  }

  confirmAddress() {
    this.cartService.updateSelectedAddress(this.addressForm.get('address').value);
  }
}
