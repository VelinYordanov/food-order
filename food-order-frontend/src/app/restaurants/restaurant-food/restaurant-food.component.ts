import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { AfterViewInit, ChangeDetectorRef, EventEmitter, Input, Output } from '@angular/core';
import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { MatChipInputEvent } from '@angular/material/chips';
import { EMPTY, Observable, of, Subject } from 'rxjs';
import { catchError, filter, first, map, switchMap, switchMapTo, tap } from 'rxjs/operators';
import { AlertService } from 'src/app/shared/services/alert.service';
import { Category } from '../models/category';
import { Food } from '../models/food';
import { price } from 'src/app/shared/validators/price-validator';
import { RestaurantService } from '../services/restaurant.service';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';

@Component({
  selector: 'app-restaurant-food',
  templateUrl: './restaurant-food.component.html',
  styleUrls: ['./restaurant-food.component.scss']
})
export class RestaurantFoodComponent implements OnInit, AfterViewInit {
  @Input() food: Food;
  @Input() categories: Category[];
  @Output('delete') onDelete = new EventEmitter<void>();
  @Output('edit') onEdit = new EventEmitter<Food>();

  filteredCategories$: Observable<Category[]>;

  foodForm: FormGroup;
  categoryFormControl: FormControl;

  separatorKeysCodes: number[] = [ENTER, COMMA];

  private editClicks = new Subject<void>();

  constructor(
    private formBuilder: FormBuilder,
    private restaurantService: RestaurantService,
    private authenticationService: AuthenticationService,
    private alertService: AlertService,
    private cdr: ChangeDetectorRef) { }

  ngOnInit(): void {
    this.foodForm = this.formBuilder.group({
      name: [this.food.name, Validators.required],
      description: [this.food.description],
      price: [this.food.price, [Validators.required, price()]],
      categories: this.formBuilder.array(this.food.categories, Validators.required)
    });

    this.categoryFormControl = this.formBuilder.control(null);

    this.editClicks
      .pipe(
        tap(_ => this.validateForm()),
        filter(_ => this.foodForm.valid),
        switchMapTo(this.authenticationService.user$),
        switchMap(user =>
          this.restaurantService.editFood(user.id, this.food.id, this.foodForm.value)
            .pipe(
              catchError(error => {
                this.alertService.displayMessage(error?.error?.description || 'An error occurred while editting food. Try again later.', 'error');
                return EMPTY;
              })
            ))
      ).subscribe(result => {
          this.alertService.displayMessage('Successfully editted food.', 'success');
          this.onEdit.emit(result);
          this.toggleForm();
      });

    this.filteredCategories$ = this.categoryFormControl.valueChanges
      .pipe(
        map(value =>
          this.categories
            .filter(category => category.name.includes(value)))
      );
  }

  ngAfterViewInit(): void {
    this.toggleForm();
    this.cdr.detectChanges();
  }

  removeCategory(category) {
    const categories = this.foodForm.get('categories') as FormArray;
    const index = categories.value.indexOf(category);
    if (index !== -1) {
      categories.removeAt(index);
    }
  }

  addCategory(event: MatChipInputEvent) {
    const value = event.value;
    this.addCategoryToFood(value);

    event.input.value = '';
  }

  selected(event: MatAutocompleteSelectedEvent, categoryInput): void {
    const value = event.option.value;
    this.addCategoryToFood(value);

    categoryInput.value = '';
  }

  toggleForm() {
    if (this.foodForm.enabled) {
      this.foodForm.disable();
      this.categoryFormControl.disable();
    } else {
      this.foodForm.enable();
      this.categoryFormControl.enable();
    }
  }

  validateForm() {
    if (!this.foodForm.valid) {
      if (this.foodForm.get('name').hasError('required')) {
        this.alertService.displayMessage('Name is required', 'error');
        return;
      }

      if (this.foodForm.get('price').hasError('price')) {
        this.alertService.displayMessage('Enter a valid price.', 'error');
        return;
      }

      if (this.foodForm.get('categories').hasError('required')) {
        this.alertService.displayMessage('At least 1 category must be added.', 'error');
        return;
      }
    }
  }

  cancel() {
    const categoriesControl = this.foodForm.get('categories') as FormArray;
    categoriesControl.clear();
    this.food.categories.forEach(category => categoriesControl.push(this.formBuilder.group(category)));

    this.foodForm.patchValue(this.food);
    this.toggleForm();
  }

  edit() {
    this.editClicks.next();
  }

  delete() {
    this.alertService.displayRequestQuestion(
      `Are you sure you want to delete food ${this.food.name}?`,
      this.deleteFood(), `Successfully deleted food ${this.food.name}`,
      `An error ocurred while deleting food ${this.food.name}. Try again later`,
      () => this.onDelete.emit());
  }

  deleteFood() {
    return this.authenticationService.user$
      .pipe(
        first(user => !!user),
        switchMap(user =>
          this.restaurantService.deleteFood(user.id, this.food.id))
      )
  }

  private addCategoryToFood(value: string) {
    const category = this.categories.find(x => x.name.toLowerCase() === value.toLowerCase());

    if (!category) {
      this.alertService.displayMessage(`Category ${value} does not exist for this restaurant. You need to create it first.`, 'error');
      return;
    }

    const categoriesArray = this.foodForm.get('categories') as FormArray;
    if (categoriesArray.value.find(x => x.name.toLowerCase() === value.toLowerCase())) {
      this.alertService.displayMessage(`Category ${value} is already added.`, 'error');
      return;
    }

    categoriesArray.push(this.formBuilder.group(category));
  }
}
