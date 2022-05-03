import { ViewportScroller } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';
import { addFoodToCartAction, selectRestaurantAction } from 'src/app/store/customers/cart/cart.actions';
import { loadRestaurantAction } from 'src/app/store/restaurants/restaurants.actions';
import { selectCurrentRestaurant } from 'src/app/store/restaurants/restaurants.selectors';
import { selectRouteParam } from 'src/app/store/router/router.selectors';
import { Category } from '../models/category';
import { Food } from '../models/food';
import { Restaurant } from '../models/restaurant';

@Component({
  selector: 'app-restaurant-details',
  templateUrl: './restaurant-details.component.html',
  styleUrls: ['./restaurant-details.component.scss']
})
export class RestaurantDetailsComponent implements OnInit, OnDestroy {
  restaurant: Restaurant;
  categoriesToView: Category[];

  private onDestroy$ = new Subject<void>();

  constructor(
    private store: Store,
    private viewScroller: ViewportScroller) { }

  ngOnInit(): void {
    this.store.select(selectRouteParam('id'))
      .pipe(takeUntil(this.onDestroy$))
      .subscribe(id => this.store.dispatch(loadRestaurantAction({ payload: id })));

    this.store.select(selectCurrentRestaurant)
      .pipe(
        takeUntil(this.onDestroy$),
        filter(restaurant => !!restaurant))
      .subscribe(restaurant => {
        this.store.dispatch(selectRestaurantAction({ payload: restaurant }));
        this.restaurant = restaurant;
        this.categoriesToView = this.restaurant.categories.filter(category => this.restaurant.foods.some(food => !!food.categories.find(c => c.id === category.id)));
      });
  }

  ngOnDestroy(): void {
    this.onDestroy$.next();
    this.onDestroy$.complete();
  }

  getFoodsForCategory(category: Category): Food[] {
    return this.restaurant.foods.filter(food => !!food.categories.find(c => c.id === category.id));
  }

  viewCategory(categoryName: string) {
    this.viewScroller.scrollToAnchor(categoryName);
  }

  addFoodToCart(food: Food) {
    this.store.dispatch(addFoodToCartAction({ payload: food }))
  }
}
