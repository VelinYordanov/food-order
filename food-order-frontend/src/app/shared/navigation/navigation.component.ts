import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { OrderRestaurant } from 'src/app/customers/models/order-restaurant';
import { User } from '../models/user';
import { updateUserAction } from '../../store/authentication/authentication.actions';
import { loggedInUserSelector } from '../../store/authentication/authentication.selectors';
import { cartItemsSumSelector, selectedRestaurantSelector } from 'src/app/store/customers/cart/cart.selectors';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.scss']
})
export class NavigationComponent implements OnInit, OnDestroy {
  user: User;
  numberOfItems: number;
  selectedRestaurant: OrderRestaurant;

  private onDestroy$ = new Subject<void>();

  constructor(
    private router: Router,
    private store: Store) { }

  ngOnInit(): void {
    this.store.select(selectedRestaurantSelector).pipe(takeUntil(this.onDestroy$)).subscribe(restaurant => this.selectedRestaurant = restaurant);
    this.store.select(cartItemsSumSelector).pipe(takeUntil(this.onDestroy$)).subscribe(numberOfItems => this.numberOfItems = numberOfItems);
    this.store.select(loggedInUserSelector)
      .pipe(
        takeUntil(this.onDestroy$)
      )
      .subscribe(user => this.user = user);
  }

  ngOnDestroy(): void {
    this.onDestroy$.next();
    this.onDestroy$.complete();
  }

  isLoggedIn() {
    return !!this.user;
  }

  isRestaurant() {
    return this.user?.authorities?.includes("ROLE_RESTAURANT");
  }

  isCustomer() {
    return this.user?.authorities?.includes("ROLE_CUSTOMER");
  }

  logOut() {
    this.store.dispatch(updateUserAction({ payload: null }));
    this.router.navigate(['restaurants']);
  }
}
