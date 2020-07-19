import { Component, Inject, OnInit } from '@angular/core';
import { Observable, of, throwError, timer } from 'rxjs';
import { catchError, filter, first, map, switchMap, tap } from 'rxjs/operators';
import { AuthenticationService } from 'src/app/shared/authentication.service';
import { SwalToken } from 'src/app/shared/injection-tokens/swal-injection-token';
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

  private deletedCategories: Category[] = [];

  constructor(
    private restaurantService: RestaurantService,
    private authenticationService: AuthenticationService,
    @Inject(SwalToken) private swal) { }

  ngOnInit(): void {
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

  openCategoryDeleteConfirmation(category: Category) {
    this.swal.fire({
      title: `Delete category ${category.name}?`,
      text: `Are you sure you want to delete category ${category.name}?`,
      icon: 'question',
      showCancelButton: true,
      allowOutsideClick: () => !this.swal.isLoading(),
      showLoaderOnConfirm: true,
      preConfirm: () => this.deleteCategory(category)
    }).then(result => {
      if (!result.isDismissed) {
        if (result?.value?.error) {
          this.swal.fire({
            title: result?.value?.error?.description || `An error occurred while deleting category ${category.name}. Try again later.`,
            icon: 'error'
          })
        } else {
          console.log(result)
          this.swal.fire({
            title: `Successfully deleted category ${category.name}`,
            icon: "success"
          })
        }
      }
    })
  }

  getCategories(categories: Category[]) {
    return categories.filter(category => !this.deletedCategories.includes(category));
  }

  remove(category: Category) {
    this.deletedCategories.push(category);
    console.log(category);
  }

  deleteCategory(category: Category) {
    return this.authenticationService.user$
      .pipe(
        filter(user => !!user),
        first(),
        switchMap(user =>
          this.restaurantService.deleteCategoryFromRestaurant(user.id, category.id)
            .pipe(
              tap(_ => this.remove(category)),
              catchError(error => of(error))
            ))
      )
  }
}
