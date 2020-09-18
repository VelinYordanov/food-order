import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, Observable, of, Subject } from 'rxjs';
import { catchError, map, switchMap, switchMapTo, withLatestFrom } from 'rxjs/operators';
import { Address } from 'src/app/customers/models/address';
import { CustomerService } from 'src/app/customers/services/customer.service';
import { AlertService } from 'src/app/shared/alert.service';
import { AuthenticationService } from 'src/app/shared/authentication.service';
import { CartService } from 'src/app/shared/cart.service';

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.scss']
})
export class CheckoutComponent implements OnInit, OnDestroy {
  private submitButtonClicks: Subject<void> = new Subject<void>();

  comment: FormControl;
  selectedAddress$: Observable<Address>;

  constructor(
    private alertService: AlertService,
    private authenticationService: AuthenticationService,
    private customerService: CustomerService,
    private cartService: CartService,
    private router: Router,
    private activatedRoute: ActivatedRoute) { }

  ngOnInit(): void {
    this.submitButtonClicks
      .pipe(
        switchMapTo(
          combineLatest([
            this.authenticationService.user$
              .pipe(
                map(user => user.id)
              ),
            this.cartService.selectedRestaurant$
              .pipe(
                map(restaurant => restaurant.id)
              ),
            this.cartService.selectedAddress$
              .pipe(
                map(address => address.id)
              ),
            this.cartService.selectedItems$
              .pipe(
                map(items => items.map(item => ({ id: item.food.id, quantity: item.quantity })))
              ),
          ])
            .pipe(
              switchMap(([customerId, restaurantId, addressId, foods]) =>
                this.customerService.submitOrder({ restaurantId, customerId, addressId, foods, comment: this.comment.value })
                  .pipe(
                    catchError(error => {
                      this.alertService.displayMessage(error?.error?.description || 'An error occurred while submitting order. Try again later', 'error');
                      return of(null);
                    })
                  ))
            )
        ))
      .subscribe(order => {
        if (order) {
          this.alertService.displayMessage('Successfully submitted order.', 'success');
        }
      })

    this.selectedAddress$ = this.cartService.selectedAddress$;
    this.comment = new FormControl(null);
  }

  ngOnDestroy(): void {
    this.submitButtonClicks.complete();
  }

  submitOrder() {
    this.submitButtonClicks.next();
  }

  getAddressData(address:Address) {
    return this.customerService.getAddressData(address);
  }
}
