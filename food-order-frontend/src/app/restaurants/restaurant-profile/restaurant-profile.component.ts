import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Observable, of, Subject } from 'rxjs';
import { catchError, filter, first, map, switchMap, switchMapTo, tap } from 'rxjs/operators';
import { AuthenticationService } from 'src/app/shared/authentication.service';
import { AlertService } from 'src/app/shared/alert.service';
import { Category } from '../models/category';
import { Restaurant } from '../models/restaurant';
import { RestaurantService } from '../services/restaurant.service';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { MatChipInputEvent } from '@angular/material/chips';
import { Food } from '../models/food';

@Component({
  selector: 'app-restaurant-profile',
  templateUrl: './restaurant-profile.component.html',
  styleUrls: ['./restaurant-profile.component.scss']
})
export class RestaurantProfileComponent implements OnInit, OnDestroy {
  restaurant: Restaurant;
  restaurantForm: FormGroup;
  separatorKeysCodes: number[] = [ENTER, COMMA];

  private editRestaurantClicksSubject = new Subject();

  constructor(
    private formBuilder: FormBuilder,
    private restaurantService: RestaurantService,
    private authenticationService: AuthenticationService,
    private alertService: AlertService) { }

  ngOnInit(): void {
    this.restaurantForm = this.formBuilder.group({
      name: [null, Validators.required],
      description: [null, Validators.required]
    })

    this.restaurantForm.disable();

    this.editRestaurantClicksSubject
      .pipe(
        switchMapTo(this.authenticationService.user$),
        switchMap(user =>
          this.restaurantService.editRestaurant(user.id, this.restaurantForm.value)
            .pipe(
              catchError(error => of(error))
            ))
      ).subscribe(result => {
        if (result?.error) {
          this.alertService.displayMessage(result?.error?.description || 'An error occurred while editting restaurant. Try again later.', 'error');
        } else {
          this.restaurantForm.disable();
          this.alertService.displayMessage('Successfully editted restaurant.', 'success');
        }
      })

    this.authenticationService.user$
      .pipe(
        first(user => !!user),
        map(user => user.id),
        switchMap(id => this.restaurantService.getRestaurantData(id))
      ).subscribe(restaurant => {
        this.restaurant = restaurant;
        this.restaurantForm.patchValue(restaurant);
      })
  }

  ngOnDestroy(): void {
    this.editRestaurantClicksSubject.complete();
  }

  toggleForm() {
    if (this.restaurantForm.enabled) {
      this.restaurantForm.disable();
    } else {
      this.restaurantForm.enable();
    }
  }

  openCategoryDeleteConfirmation(category: Category) {
    this.alertService.displayRequestQuestion(
      `Are you sure you want to delete category ${category.name}?`,
      () => this.deleteCategory(category),
      `Successfully deleted category ${category.name}`,
      `An error occurred while deleting category ${category.name}. Try again later.`);
  }

  deleteCategory(category: Category) {
    return this.authenticationService.user$
      .pipe(
        filter(user => !!user),
        first(),
        switchMap(user =>
          this.restaurantService.deleteCategoryFromRestaurant(user.id, category.id)
            .pipe(
              tap(_ => this.removeCategory(category)),
              catchError(error => of(error))
            ))
      )
  }

  addCategory(categoryName: string) {
    return this.authenticationService.user$
      .pipe(
        first(user => !!user),
        switchMap(user =>
          this.restaurantService.addCategoryToRestaurant(user.id, categoryName)
            .pipe(
              tap(category => this.restaurant.categories.push(category)),
              catchError(error => of(error))
            ))
      )
  }

  openCategoryAdditionConfirmation(event: MatChipInputEvent) {
    const categoryName = event.value;

    this.alertService.displayRequestQuestion(
      `Are you sure you want to create category ${categoryName}?`,
      () => this.addCategory(categoryName),
      `Successfully created category ${categoryName}`,
      `An error occurred while creating category ${categoryName}. Try again later.`,
      () => event.input && (event.input.value = ''));
  }

  editRestaurant() {
    this.editRestaurantClicksSubject.next();
  }

  private removeCategory(category: Category) {
    const index = this.restaurant.categories.findIndex(x => x.id === category.id);
    if (index !== -1) {
      this.restaurant.categories.splice(index, 1);
    }
  }

  private removeFood(food: Food) {
    const index = this.restaurant.foods.findIndex(x => x.id === food.id);
    if (index !== -1) {
      this.restaurant.foods.splice(index, 1);
    }
  }
}
