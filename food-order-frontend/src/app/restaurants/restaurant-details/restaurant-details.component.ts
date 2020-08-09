import { ViewportScroller } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { AlertService } from 'src/app/shared/alert.service';
import { Category } from '../models/category';
import { Food } from '../models/food';
import { Restaurant } from '../models/restaurant';
import { RestaurantService } from '../services/restaurant.service';

@Component({
  selector: 'app-restaurant-details',
  templateUrl: './restaurant-details.component.html',
  styleUrls: ['./restaurant-details.component.scss']
})
export class RestaurantDetailsComponent implements OnInit {
  restaurant: Restaurant;
  categoriesToView: Category[];

  constructor(
    private activatedRoute: ActivatedRoute,
    private restaurantService: RestaurantService,
    private alertService: AlertService,
    private viewScroller: ViewportScroller) { }

  ngOnInit(): void {
    this.activatedRoute.paramMap
      .pipe(
        switchMap(paramMap =>
          this.restaurantService.getRestaurantData(paramMap.get('id'))
            .pipe(
              catchError(error => {
                this.alertService.displayMessage(error?.error?.description || 'An error ocurred while loading restaurant data. Try again later.', 'error');
                return throwError(error);
              })
            ))
      ).subscribe(restaurant => {
        this.restaurant = restaurant;
        this.categoriesToView = this.restaurant.categories.filter(category => this.restaurant.foods.some(food => !!food.categories.find(c => c.id === category.id)));
      });
  }

  getFoodsForCategory(category: Category): Food[] {
    return this.restaurant.foods.filter(food => !!food.categories.find(c => c.id === category.id));
  }

  viewCategory(categoryName: string) {
    this.viewScroller.scrollToAnchor(categoryName);
  }
}
