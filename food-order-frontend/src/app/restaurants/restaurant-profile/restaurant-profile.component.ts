import { Component, OnInit } from '@angular/core';
import { Observable, of, timer } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';
import { AuthenticationService } from 'src/app/shared/authentication.service';
import { Category } from '../models/category';
import { Restaurant } from '../models/restaurant';
import { RestaurantService } from '../services/restaurant.service';

@Component({
  selector: 'app-restaurant-profile',
  templateUrl: './restaurant-profile.component.html',
  styleUrls: ['./restaurant-profile.component.scss']
})
export class RestaurantProfileComponent implements OnInit {
  restaurant$: Observable<Restaurant>;
  deleteCategoryFunction;

  private deletedCategories: Category[] = [];
  private categoryToBeDeletedId: string;

  constructor(
    private restaurantService: RestaurantService,
    private authenticationService: AuthenticationService) { }

  ngOnInit(): void {
    this.deleteCategoryFunction = this.deleteCategory.bind(this);

    this.restaurant$ = this.authenticationService.user$
      .pipe(
        map(user => user.id),
        switchMap(id =>
          this.restaurantService.getRestaurantData(id)
            .pipe(
              catchError(_ => of(null))
            )
        ))
  }

  getCategories(categories: Category[]) {
    return categories.filter(category => !this.deletedCategories.includes(category));
  }

  remove(category: Category) {
    this.deletedCategories.push(category);
    console.log(category);
  }

  deleteCategory() {
    console.log(this.categoryToBeDeletedId);
    return timer(2000).pipe(map(x => null));
  }

  setCategoryToBeDeleted(categoryId: string) {
    this.categoryToBeDeletedId = categoryId;
  }
}
