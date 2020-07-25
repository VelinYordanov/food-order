import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Observable, of, Subject } from 'rxjs';
import { catchError, filter, first, map, switchMap, switchMapTo, tap } from 'rxjs/operators';
import { AuthenticationService } from 'src/app/shared/authentication.service';
import { AlertService } from 'src/app/shared/alert.service';
import { SwalToken } from 'src/app/shared/injection-tokens/swal-injection-token';
import { Category } from '../models/category';
import { Restaurant } from '../models/restaurant';
import { RestaurantService } from '../services/restaurant.service';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { MatChipInputEvent } from '@angular/material/chips';

@Component({
  selector: 'app-restaurant-profile',
  templateUrl: './restaurant-profile.component.html',
  styleUrls: ['./restaurant-profile.component.scss']
})
export class RestaurantProfileComponent implements OnInit, OnDestroy {
  restaurant$: Observable<Restaurant>;
  restaurantForm: FormGroup;
  separatorKeysCodes: number[] = [ENTER, COMMA];
  categories: Category[];

  private editRestaurantClicksSubject = new Subject();
  private deletedCategories: Category[] = [];

  constructor(
    private formBuilder: FormBuilder,
    private restaurantService: RestaurantService,
    private authenticationService: AuthenticationService,
    private alertService: AlertService,
    @Inject(SwalToken) private swal) { }

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
        if (result?.error?.description) {
          this.swal.fire({
            title: result.error.description,
            icon: "error"
          })
        } else {
          this.restaurantForm.disable();
          this.swal.fire({
            title: "Successfully editted restaurant",
            icon: "success"
          })
        }
      })

    this.restaurant$ = this.authenticationService.user$
      .pipe(
        map(user => user.id),
        switchMap(id =>
          this.restaurantService.getRestaurantData(id)
            .pipe(
              tap(restaurant => this.restaurantForm.patchValue(restaurant)),
              tap(restaurant => this.categories = restaurant.categories),
              catchError(_ => of(null))
            )
        ))
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

  getCategories() {
    return this.categories.filter(category => !this.deletedCategories.includes(category));
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

  addCategory(categoryName: string) {
    return this.authenticationService.user$
      .pipe(
        filter(user => !!user),
        first(),
        switchMap(user =>
          this.restaurantService.addCategoryToRestaurant(user.id, categoryName)
            .pipe(
              tap(category => this.add(category)),
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

  private remove(category: Category) {
    this.deletedCategories.push(category);
    console.log(category);
  }

  private add(category: Category) {
    this.categories.push(category);
  }
}
