import { AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Observable, of, Subject } from 'rxjs';
import { catchError, filter, first, map, startWith, switchMap, switchMapTo, tap } from 'rxjs/operators';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
import { AlertService } from 'src/app/shared/services/alert.service';
import { Category } from '../models/category';
import { Restaurant } from '../models/restaurant';
import { RestaurantService } from '../services/restaurant.service';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { MatChipInputEvent } from '@angular/material/chips';
import { Food } from '../models/food';
import { RestaurantAddFoodDialogComponent } from '../restaurant-add-food-dialog/restaurant-add-food-dialog.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-restaurant-profile',
  templateUrl: './restaurant-profile.component.html',
  styleUrls: ['./restaurant-profile.component.scss']
})
export class RestaurantProfileComponent implements OnInit, OnDestroy {
  readonly separatorKeysCodes: number[] = [ENTER, COMMA];

  restaurant: Restaurant;

  restaurantForm: FormGroup;
  search: FormControl;

  filteredFoods$: Observable<Food[]>;

  private editRestaurantClicksSubject = new Subject();

  constructor(
    private formBuilder: FormBuilder,
    private restaurantService: RestaurantService,
    private authenticationService: AuthenticationService,
    private dialog: MatDialog,
    private alertService: AlertService) { }

  ngOnInit(): void {
    this.restaurantForm = this.formBuilder.group({
      name: [null, Validators.required],
      description: [null, Validators.required],
      category: [null],
    });

    this.search = this.formBuilder.control('');

    this.restaurantForm.disable();

    this.filteredFoods$ = this.search.valueChanges
      .pipe(
        startWith(''),
        map(searchValue => this.restaurant.foods.filter(food => food.name.toLowerCase().includes(searchValue.toLowerCase())))
      )

    this.editRestaurantClicksSubject
      .pipe(
        switchMapTo(this.authenticationService.user$),
        switchMap(user =>
          this.restaurantService.editRestaurant(user.id, this.restaurantForm.value)
            .pipe(
              catchError(error => {
                this.alertService.displayMessage(error?.error?.description || 'An error occurred while editting restaurant. Try again later.', 'error');
                return of(null);
              })
            ))
      ).subscribe(result => {
        if (result) {
          this.restaurant = result;
          this.search.updateValueAndValidity();
          this.restaurantForm.disable();
          this.alertService.displayMessage('Successfully editted restaurant.', 'success');
        }
      })

    this.authenticationService.user$
      .pipe(
        first(user => !!user),
        map(user => user.id),
        switchMap(id =>
          this.restaurantService.getRestaurantData(id)
            .pipe(
              catchError(error => {
                this.alertService.displayMessage(error?.error?.description || 'An error occurred while loading your profile. Try again later.', 'error');
                return of(null);
              })
            ))
      ).subscribe(restaurant => {
        if (restaurant) {
          this.restaurant = restaurant;
          this.restaurantForm.patchValue(restaurant);
        }
      })
  }

  ngOnDestroy(): void {
    this.editRestaurantClicksSubject.complete();
  }

  cancel() {
    this.restaurantForm.disable();
    this.restaurantForm.patchValue(this.restaurant);
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(RestaurantAddFoodDialogComponent, {
      data: this.restaurant.categories
    });

    dialogRef.afterClosed()
      .subscribe(food => {
        if (food) {
          this.restaurant.foods.push(food);
          food.categories
            .filter(category => !this.restaurant.categories
              .some(c => c.id === category.id))
            .forEach(category => this.restaurant.categories.push(category));
          this.search.updateValueAndValidity();
        }
      });
  }

  toggleForm() {
    if (this.restaurantForm.enabled) {
      this.restaurantForm.disable();
    } else {
      this.restaurantForm.enable();
    }
  }

  editFood(food: Food) {
    const index = this.restaurant.foods.findIndex(f => f.id === food.id);
    if (index !== -1) {
      this.restaurant.foods.splice(index, 1, food);
      this.search.updateValueAndValidity();
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
      this.search.updateValueAndValidity();
    }
  }
}
