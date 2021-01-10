import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { filter, map, scan, tap } from 'rxjs/operators';
import { OrderRestaurant } from 'src/app/customers/models/order-restaurant';
import { User } from '../models/user';
import { AuthenticationService } from '../services/authentication.service';
import { CartService } from '../services/cart.service';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.scss']
})
export class NavigationComponent implements OnInit {
  user: User;
  numberOfItems:number;
  selectedRestaurant: OrderRestaurant;

  constructor(
    private authenticationService: AuthenticationService,
    private cartService: CartService,
    private router: Router) { }

  ngOnInit(): void {
    this.cartService.selectedRestaurant$.subscribe(restaurant => this.selectedRestaurant = restaurant);

    this.cartService.selectedItems$
    .pipe(
      map(items => items.map(x => x.quantity).reduce((acc, current) => acc + current, 0)),
    ).subscribe(numberOfItems => this.numberOfItems = numberOfItems);

    this.authenticationService.user$.subscribe(user => this.user = user);
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
    this.authenticationService.logout();
    this.router.navigate(['restaurants']);
  }
}
