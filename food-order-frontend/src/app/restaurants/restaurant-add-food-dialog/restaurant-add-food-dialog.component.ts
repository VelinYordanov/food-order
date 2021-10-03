import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { MatChipInputEvent } from '@angular/material/chips';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { EMPTY, Observable, of, Subject } from 'rxjs';
import { catchError, filter, finalize, first, map, switchMap, tap, withLatestFrom } from 'rxjs/operators';
import { AlertService } from 'src/app/shared/services/alert.service';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
import { price } from 'src/app/shared/validators/price-validator';
import { Category } from '../models/category';
import { Food } from '../models/food';
import { RestaurantService } from '../services/restaurant.service';

@Component({
  selector: 'app-restaurant-add-food-dialog',
  templateUrl: './restaurant-add-food-dialog.component.html',
  styleUrls: ['./restaurant-add-food-dialog.component.scss']
})
export class RestaurantAddFoodDialogComponent implements OnInit, OnDestroy {
  foodForm: FormGroup;
  categoryFormControl: FormControl;
  filteredCategories$: Observable<Category[]>;

  separatorKeysCodes: number[] = [ENTER, COMMA];

  private addButtonClicks = new Subject<Food>();

  constructor(
    private formBuilder: FormBuilder,
    private alertService: AlertService,
    private restaurantService: RestaurantService,
    private authenticationService: AuthenticationService,
    private dialogRef: MatDialogRef<RestaurantAddFoodDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private categories: Category[],
  ) { }

  ngOnInit(): void {
    this.addButtonClicks.pipe(
      withLatestFrom(this.authenticationService.user$),
      tap(_ => this.foodForm.disable()),
      switchMap(([food, restaurant]) =>
        this.restaurantService.addFood(restaurant.id, food)
          .pipe(
            finalize(() => this.foodForm.enable()),
            catchError(error => {
              this.alertService.displayMessage(error?.error?.description || 'An error occurred while adding food. Try again later.', 'error');
              return EMPTY;
            }),
          ))
    ).subscribe(food => {
      this.alertService.displayMessage(`Successfully added food ${food.name}`, 'success');
      this.dialogRef.close(food);
    });

    this.foodForm = this.formBuilder.group({
      name: [null, Validators.required],
      description: [null],
      price: [null, [Validators.required, price()]],
      categories: this.formBuilder.array([], Validators.required)
    });

    this.categoryFormControl = this.formBuilder.control(null);

    this.filteredCategories$ = this.categoryFormControl.valueChanges
      .pipe(
        map(value =>
          this.categories
            .filter(category => category.name.includes(value)))
      );
  }

  ngOnDestroy() {
    this.addButtonClicks.complete();
  }

  addCategory(event: MatChipInputEvent) {
    const value = event.value;
    this.addCategoryToFood(value).subscribe(_ => event.input.value = '');
  }

  selected(event: MatAutocompleteSelectedEvent, categoryInput) {
    const value = event.option.value;
    this.addCategoryToFood(value).subscribe(_ => categoryInput.value = '');
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

  private addCategoryToFood(value: string): Observable<Category> {
    let category = this.categories.find(x => x.name.toLowerCase() === value.toLowerCase());
    let category$: Observable<Category> = of(category);

    if (!category) {
      category$ = this.alertService.displayQuestion(`Category ${value} does not exist. Do you want to create it?`)
        .pipe(
          filter(x => !!x),
          switchMap(_ => this.createCategory(value)
            .pipe(
              catchError(error => {
                this.alertService.displayMessage(error?.error?.description || 'An error occurred while creating category.', 'error');
                return EMPTY;
              })
            ))
        )
    }

    return category$.pipe(
      tap(resultCategory => {
        const categoriesArray = this.foodForm.get('categories') as FormArray;
        if (categoriesArray.value.find(x => x.name.toLowerCase() === value.toLowerCase())) {
          this.alertService.displayMessage(`Category ${resultCategory?.name} is already added.`, 'error');
          return;
        }

        categoriesArray.push(this.formBuilder.group(resultCategory));
      })
    );
  }

  private createCategory(categoryName: string) {
    return this.authenticationService.user$
      .pipe(
        first(user => !!user),
        switchMap(user =>
          this.restaurantService.addCategoryToRestaurant(user.id, categoryName)
            .pipe(
              tap(category => this.categories.push(category)),
            ))
      )
  }
}
