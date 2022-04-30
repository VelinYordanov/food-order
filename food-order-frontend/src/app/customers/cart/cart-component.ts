import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable, Subject } from 'rxjs';
import { first, map, takeUntil } from 'rxjs/operators';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
import { CartService } from 'src/app/shared/services/cart.service';
import { cartItemsSumSelector } from '../store/cart/cart.selectors';

@Component({
  selector: 'app-cart',
  templateUrl: './cart-component.html',
  styleUrls: ['./cart-component.scss'],
})
export class CartComponent implements OnInit {
  numberOfFoods$: Observable<number>;

  constructor(
    private router: Router, 
    private store: Store,
    private authenticationService : AuthenticationService) {}

  ngOnInit(): void {
    this.numberOfFoods$ = this.store.select(cartItemsSumSelector);
  }

  goToAddress() {
    // TODO: use store here
    this.authenticationService.user$
    .pipe(
      first(),
    ).subscribe(user => user ? this.router.navigate(['customer', 'order', 'address']) : this.router.navigate(['login']));
  }
}
