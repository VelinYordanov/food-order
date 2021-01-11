import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, EMPTY, Observable } from 'rxjs';
import { catchError, first, map, switchMap } from 'rxjs/operators';
import { AlertService } from 'src/app/shared/services/alert.service';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
import { User } from 'src/app/shared/models/user';
import { Address } from '../models/address';
import { CustomerService } from '../services/customer.service';

@Component({
  selector: 'app-address-update',
  templateUrl: './address-update.component.html',
  styleUrls: ['./address-update.component.scss']
})
export class AddressUpdateComponent implements OnInit {
  address: Address;

  private currentUser$: Observable<User>;
  private currentAddressId$: Observable<string>;

  constructor(
    private router:Router,
    private activatedRoute: ActivatedRoute,
    private authenticationService: AuthenticationService,
    private customerService: CustomerService,
    private alertService: AlertService,
  ) { }

  ngOnInit(): void {
    this.currentUser$ = this.authenticationService.user$.pipe(first(user => !!user));
    this.currentAddressId$ = this.activatedRoute.paramMap
      .pipe(
        map(paramMap => paramMap.get('id')),
        first(id => !!id)
      );

    combineLatest([
      this.currentUser$,
      this.currentAddressId$
    ])
      .pipe(
        switchMap(([user, id]) =>
          this.customerService.getCustomerAddress(user.id, id)
            .pipe(
              catchError(error => {
                this.alertService.displayMessage('An error occurred while loading address. Try again later.', 'error');
                return EMPTY;
              })
            ))
      ).subscribe(address => this.address = address);
  }

  updateAddress(address: Address) {
    combineLatest([
      this.currentUser$,
      this.currentAddressId$
    ])
      .pipe(
        switchMap(([user, addressId]) =>
          this.customerService.editCustomerAddress(user.id, addressId, address)
          .pipe(
            catchError(error => {
              this.alertService.displayMessage(error?.error?.description || 'An error occurred while editting address. Try again later.', 'error');
              return EMPTY;
            })
          ))
      ).subscribe(address => {
        this.alertService.displayMessage('Successfully editted address.', 'success');
        this.router.navigate(['customer', 'profile']);
      })
  }
}
