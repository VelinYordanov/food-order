import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { map } from 'rxjs/operators';
import { User } from '../models/user';
import { AuthenticationService } from '../services/authentication.service';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.scss']
})
export class NavigationComponent implements OnInit {
  user: User;

  constructor(
    private authenticationService: AuthenticationService,
    private router: Router) { }

  ngOnInit(): void {
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
