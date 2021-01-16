import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { EMPTY, Observable, Subscription } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { Address } from 'src/app/customers/models/address';
import { CustomerService } from 'src/app/customers/services/customer.service';
import { AlertService } from 'src/app/shared/services/alert.service';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
import { CartService } from 'src/app/shared/services/cart.service';
import { UtilService } from 'src/app/shared/services/util.service';

@Component({
  selector: 'app-address-select',
  templateUrl: './address-select.component.html',
  styleUrls: ['./address-select.component.scss'],
})
export class AddressSelectComponent
  implements OnInit, AfterViewInit, OnDestroy {
  addressForm: FormGroup;
  addresses$: Observable<Address[]>;

  private selectedAddressSubscription: Subscription;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private formBuilder: FormBuilder,
    private cartService: CartService,
    private authenticationService: AuthenticationService,
    private alertService: AlertService,
    private customerService: CustomerService,
    private utilService: UtilService
  ) {}

  ngOnInit(): void {
    this.addressForm = this.formBuilder.group({
      address: [null, Validators.required],
    });

    this.addressForm.valueChanges.subscribe(console.log);

    this.addresses$ = this.authenticationService.user$.pipe(
      switchMap((user) =>
        this.customerService.getCustomerAddresses(user.id).pipe(
          catchError((error) => {
            this.alertService.displayMessage(
              error?.error?.description ||
                'An error occurred while loading addresses. Try again later.',
              'error'
            );
            return EMPTY;
          })
        )
      )
    );
  }

  ngAfterViewInit(): void {
    this.selectedAddressSubscription = this.cartService.selectedAddress$.subscribe(
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
    this.selectedAddressSubscription.unsubscribe();
  }
}
