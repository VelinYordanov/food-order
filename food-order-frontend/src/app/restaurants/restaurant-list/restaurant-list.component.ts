import { Component, OnInit } from '@angular/core';
import { EMPTY, Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AlertService } from 'src/app/shared/services/alert.service';
import { RestaurantListItem } from '../models/restaurant-list-item';
import { RestaurantService } from '../services/restaurant.service';

@Component({
  selector: 'app-restaurant-list',
  templateUrl: './restaurant-list.component.html',
  styleUrls: ['./restaurant-list.component.scss']
})
export class RestaurantListComponent implements OnInit {
  restaurants$: Observable<RestaurantListItem[]>;

  constructor(
    private restaurantService: RestaurantService,
    private alertService: AlertService) { }

  ngOnInit(): void {
    this.restaurants$ = this.restaurantService.getRestaurantsList()
      .pipe(
        catchError(error => {
          this.alertService.displayMessage(error?.error?.desccription || 'An error occurred while loading restaurants data. Try again later.', 'error');
          return EMPTY;
        })
      );
  }
}
