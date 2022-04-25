import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { Subject } from 'rxjs';
import { filter, map, scan, takeUntil, tap } from 'rxjs/operators';
import { OrderRestaurant } from 'src/app/customers/models/order-restaurant';
import { User } from '../models/user';
import { CartService } from '../services/cart.service';
import { updateUserAction } from '../store/authentication/authentication.actions';
import { loggedInUserSelector } from '../store/authentication/authentication.selectors';

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
    private cartService: CartService,
    private router: Router,
    private store: Store) { }

  ngOnInit(): void {
    this.cartService.selectedRestaurant$.subscribe(restaurant => this.selectedRestaurant = restaurant);

    this.cartService.selectedItems$
      .pipe(
        map(items => items.map(x => x.quantity).reduce((acc, current) => acc + current, 0)),
      ).subscribe(numberOfItems => this.numberOfItems = numberOfItems);

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
    this.store.dispatch(updateUserAction({ user: null }));
    this.router.navigate(['restaurants']);
  }
}
