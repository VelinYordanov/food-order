import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { MatChipInputEvent } from '@angular/material/chips';
import { MatDialogRef } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { Observable, Subject } from 'rxjs';
import { filter, map, takeUntil, withLatestFrom } from 'rxjs/operators';
import { price } from 'src/app/shared/validators/price-validator';
import { loggedInUserIdSelector } from 'src/app/store/authentication/authentication.selectors';
import { addCategoryToRestaurantPromptAction, addCategoryToRestaurantSuccessAction, addFoodToRestaurantAction, addFoodToRestaurantErrorAction, addFoodToRestaurantSuccessAction } from 'src/app/store/restaurants/restaurants.actions';
import { Category } from '../models/category';
import { Food } from '../models/food';
import { Actions, ofType } from "@ngrx/effects";
import { selectRestaurantCategories } from 'src/app/store/restaurants/restaurants.selectors';
import { AlertService } from 'src/app/shared/services/alert.service';

@Component({
  selector: 'app-restaurant-add-food-dialog',
  templateUrl: './restaurant-add-food-dialog.component.html',
  styleUrls: ['./restaurant-add-food-dialog.component.scss']
})
export class RestaurantAddFoodDialogComponent implements OnInit, OnDestroy {
  foodForm: FormGroup;
  categoryFormControl: FormControl;
  filteredCategories$: Observable<Category[]>;

  @ViewChild('categoryInput') categoryInput: ElementRef<HTMLInputElement>;

  readonly separatorKeysCodes: number[] = [ENTER, COMMA];

  private readonly addButtonClicks = new Subject<Food>();
  private readonly addCategoryClicks = new Subject<string>();
  private readonly onDestroy$ = new Subject<void>();

  constructor(
    private store: Store,
    private actions$: Actions,
    private alertService: AlertService,
    private formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<RestaurantAddFoodDialogComponent>
  ) { }

  ngOnInit(): void {
    this.addCategoryClicks
      .pipe(
        filter(categoryName => {
          const categoriesArray = this.foodForm.get('categories') as FormArray;
          if (categoriesArray.value.find(x => x.name.toLowerCase() === categoryName.toLowerCase())) {
            this.alertService.displayMessage(`Category ${categoryName} is already added.`, 'error');
            return false;
          }

          return true;
        }),
        withLatestFrom(this.store.select(selectRestaurantCategories), this.store.select(loggedInUserIdSelector)),
      ).subscribe(([categoryName, categories, restaurantId]) => {
        const category = categories.find(c => c.name.toLowerCase() === categoryName.toLowerCase());
        if (category) {
          const categoriesArray = this.foodForm.get('categories') as FormArray;
          categoriesArray.push(this.formBuilder.group(category));
          this.clearInput();
          return
        }

        const data = { restaurantId, categoryName };

        const payload = {
          promptQuestion: `Category ${categoryName} does not exist. Do you want to create it?`,
          errorText: 'An error occurred while creating category.',
          successText: "Successfuly added category.",
          data: data
        };

        this.store.dispatch(addCategoryToRestaurantPromptAction({ payload }));
      });

    this.actions$.pipe(
      takeUntil(this.onDestroy$),
      ofType(addCategoryToRestaurantSuccessAction)
    ).subscribe(action => {
      const categoriesArray = this.foodForm.get('categories') as FormArray;
      categoriesArray.push(this.formBuilder.group(action.payload.data));
      this.clearInput();
    })

    this.addButtonClicks.pipe(
      withLatestFrom(this.store.select(loggedInUserIdSelector)),
    ).subscribe(([food, restaurantId]) => {
      this.foodForm.disable();
      const payload = { food, restaurantId };
      this.store.dispatch(addFoodToRestaurantAction({ payload }));
    });

    this.actions$.pipe(
      ofType(addFoodToRestaurantSuccessAction, addFoodToRestaurantErrorAction)
    ).subscribe(action => this.foodForm.enable());

    this.actions$.pipe(
      ofType(addFoodToRestaurantSuccessAction)
    ).subscribe(action => this.dialogRef.close())

    this.foodForm = this.formBuilder.group({
      name: [null, Validators.required],
      description: [null],
      price: [null, [Validators.required, price()]],
      categories: this.formBuilder.array([], Validators.required)
    });

    this.categoryFormControl = this.formBuilder.control(null);

    this.filteredCategories$ = this.categoryFormControl.valueChanges
      .pipe(
        withLatestFrom(this.store.select(selectRestaurantCategories)),
        map(([value, categories]) => categories.filter(category => category.name.includes(value)))
      );
  }

  ngOnDestroy() {
    this.onDestroy$.next();
    this.onDestroy$.complete();
    this.addButtonClicks.complete();
    this.addCategoryClicks.complete();
  }

  addCategory(event: MatChipInputEvent) {
    this.addCategoryClicks.next(event.value);
  }

  selected(event: MatAutocompleteSelectedEvent, categoryInput) {
    this.addCategoryClicks.next(event.option.value);
  }

  add() {
    if (this.foodForm.valid) {
      this.addButtonClicks.next(this.foodForm.value);
    }
  }

  close() {
    this.dialogRef.close();
  }

  removeCategory(category) {
    const categories = this.foodForm.get('categories') as FormArray;
    const index = categories.value.indexOf(category);
    if (index !== -1) {
      categories.removeAt(index);
    }
  }

  private clearInput() {
    this.categoryFormControl.setValue(null);
    this.categoryInput.nativeElement.value = '';
  }
}
