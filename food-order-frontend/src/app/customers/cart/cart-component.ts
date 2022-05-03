import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { first } from 'rxjs/operators';
import { loggedInUserSelector } from 'src/app/store/authentication/authentication.selectors';
import { cartItemsSumSelector } from 'src/app/store/customers/cart/cart.selectors';

@Component({
  selector: 'app-cart',
  templateUrl: './cart-component.html',
  styleUrls: ['./cart-component.scss'],
})
export class CartComponent implements OnInit {
  numberOfFoods$: Observable<number>;

  constructor(
    private router: Router, 
    private store: Store) {}

  ngOnInit(): void {
    this.numberOfFoods$ = this.store.select(cartItemsSumSelector);
  }

  goToAddress() {
    this.store.select(loggedInUserSelector)
    .pipe(
      first(),
    ).subscribe(user => user ? this.router.navigate(['customer', 'order', 'address']) : this.router.navigate(['login']));
  }
}
