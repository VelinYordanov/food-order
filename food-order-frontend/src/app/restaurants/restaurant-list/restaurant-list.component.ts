import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { loadRestaurantsAction } from 'src/app/store/restaurants/restaurants.actions';
import { selectRestaurants } from 'src/app/store/restaurants/restaurants.selectors';
import { RestaurantListItem } from '../models/restaurant-list-item';

@Component({
  selector: 'app-restaurant-list',
  templateUrl: './restaurant-list.component.html',
  styleUrls: ['./restaurant-list.component.scss']
})
export class RestaurantListComponent implements OnInit {
  restaurants$: Observable<RestaurantListItem[]>;

  constructor(
    private store: Store) { }

  ngOnInit(): void {
    this.store.dispatch(loadRestaurantsAction());
    this.restaurants$ = this.store.select(selectRestaurants);
  }
}
