import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { map, takeUntil } from 'rxjs/operators';
import { CartService } from 'src/app/shared/services/cart.service';

@Component({
  selector: 'app-cart',
  templateUrl: './cart-component.html',
  styleUrls: ['./cart-component.scss'],
})
export class CartComponent implements OnInit, OnDestroy {
  numberOfFoods: number;

  private onDestroy$ = new Subject<void>();

  constructor(private router: Router, private cartService: CartService) {}

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
    this.router.navigate(['order', 'address']);
  }

  ngOnDestroy(): void {
    this.onDestroy$.next();
    this.onDestroy$.complete();
  }
}
