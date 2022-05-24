import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Observable, Subject } from 'rxjs';
import { map, mapTo, startWith, takeUntil, withLatestFrom } from 'rxjs/operators';
import { Category } from '../models/category';
import { Restaurant } from '../models/restaurant';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { MatChipInputEvent } from '@angular/material/chips';
import { Food } from '../models/food';
import { RestaurantAddFoodDialogComponent } from '../restaurant-add-food-dialog/restaurant-add-food-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { activateAction, deactivateAction, subscribeToRestaurantOrdersAction, unsubscribeFromRestaurantOrdersAction } from 'src/app/store/notifications/notification.actions';
import { loggedInUserIdSelector } from 'src/app/store/authentication/authentication.selectors';
import { addCategoryToRestaurantPromptAction, deleteCategoryFromRestaurantPromptAction, editRestaurantAction, loadRestaurantAction } from 'src/app/store/restaurants/restaurants.actions';
import { selectCurrentRestaurant } from 'src/app/store/restaurants/restaurants.selectors';

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

  private readonly categoryDeletes$ = new Subject<Category>();
  private readonly categoryAdditions$ = new Subject<MatChipInputEvent>();

  private editRestaurantClicksSubject = new Subject();
  private cancel$ = new Subject<void>();

  constructor(
    private store: Store,
    private formBuilder: FormBuilder,
    private dialog: MatDialog) { }

  ngOnInit(): void {
    this.restaurantForm = this.formBuilder.group({
      name: [null, Validators.required],
      description: [null, Validators.required],
      category: [null],
    });

    this.store.dispatch(activateAction());
    this.store.select(loggedInUserIdSelector)
      .pipe(takeUntil(this.cancel$))
      .subscribe(id => this.store.dispatch(subscribeToRestaurantOrdersAction({ payload: id })));

    this.search = this.formBuilder.control('');

    this.restaurantForm.disable();

    this.filteredFoods$ = this.search.valueChanges
      .pipe(
        startWith(''),
        map(searchValue => this.restaurant.foods.filter(food => food.name.toLowerCase().includes(searchValue.toLowerCase())))
      )

    this.setupRestaurantEdits();
    this.setupRestaurantProfile();
    this.setupCategoryDeletes();
    this.setupCategoryAdditions();
  }

  ngOnDestroy(): void {
    this.editRestaurantClicksSubject.complete();
    this.categoryAdditions$.complete();
    this.categoryDeletes$.complete();
    this.cancel$.next();
    this.cancel$.complete();
    this.store.dispatch(unsubscribeFromRestaurantOrdersAction());
    this.store.dispatch(deactivateAction());
  }

  cancel() {
    this.restaurantForm.disable();
    this.restaurantForm.patchValue(this.restaurant);
  }

  openDialog(): void {
    this.dialog.open(RestaurantAddFoodDialogComponent);

    // dialogRef.afterClosed()
    //   .subscribe(food => {
    //       this.search.updateValueAndValidity();
    //     });
  }

  toggleForm() {
    if (this.restaurantForm.enabled) {
      this.restaurantForm.disable();
    } else {
      this.restaurantForm.enable();
    }
  }

  openCategoryDeleteConfirmation(category: Category) {
    this.categoryDeletes$.next(category);
  }

  openCategoryAdditionConfirmation(event: MatChipInputEvent) {
    this.categoryAdditions$.next(event);
  }

  editRestaurant() {
    this.editRestaurantClicksSubject.next();
  }

  private setupRestaurantEdits() {
    this.editRestaurantClicksSubject
      .pipe(
        map(_ => this.restaurantForm.value)
      ).subscribe(restaurant => this.store.dispatch(editRestaurantAction({ payload: restaurant })));

    this.store.select(selectCurrentRestaurant)
      .pipe(
        takeUntil(this.cancel$)
      ).subscribe(restaurant => {
        this.restaurantForm.patchValue(restaurant);
        this.restaurant = restaurant;
        this.search.updateValueAndValidity();
        this.restaurantForm.disable();
      });
  }

  private setupRestaurantProfile() {
    this.store.select(loggedInUserIdSelector)
      .pipe(
        takeUntil(this.cancel$)
      ).subscribe(id => this.store.dispatch(loadRestaurantAction({ payload: id })));
  }

  private setupCategoryDeletes() {
    this.categoryDeletes$.subscribe(category => this.store.dispatch(deleteCategoryFromRestaurantPromptAction({ payload: category })));
  }

  private setupCategoryAdditions() {
    this.categoryAdditions$
      .pipe(
        withLatestFrom(this.store.select(loggedInUserIdSelector))
      ).subscribe(([event, restaurantId]) => {
        const data = { restaurantId, categoryName: event.value };
        const payload = {
          promptQuestion: `Are you sure you want to create category ${event.value}?`,
          successText: `Successfully created category ${event.value}`,
          errorText: `An error occurred while creating category ${event.value}. Try again later.`,
          data: data
        }

        this.store.dispatch(addCategoryToRestaurantPromptAction({ payload }));
      })
  }
}
