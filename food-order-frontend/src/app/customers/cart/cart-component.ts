import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { first, map, takeUntil } from 'rxjs/operators';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
import { CartService } from 'src/app/shared/services/cart.service';

@Component({
  selector: 'app-cart',
  templateUrl: './cart-component.html',
  styleUrls: ['./cart-component.scss'],
})
export class CartComponent implements OnInit, OnDestroy {
  numberOfFoods: number;

  private onDestroy$ = new Subject<void>();

  constructor(
    private router: Router, 
    private cartService: CartService,
    private authenticationService : AuthenticationService) {}

  ngOnInit(): void {
    this.cartService.selectedItems$
      .pipe(
        takeUntil(this.onDestroy$),
        map((items) =>
          items
            .map((item) => item.quantity)
            .reduce((total, current) => total + current, 0)
        )
      )
      .subscribe((numberOfFoods) => (this.numberOfFoods = numberOfFoods));
  }

  goToAddress() {
    this.authenticationService.user$
    .pipe(
      first(),
    ).subscribe(user => user ? this.router.navigate(['customer', 'order', 'address']) : this.router.navigate(['login']));
  }

  ngOnDestroy(): void {
    this.onDestroy$.next();
    this.onDestroy$.complete();
  }
}
