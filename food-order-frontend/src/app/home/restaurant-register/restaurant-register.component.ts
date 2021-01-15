import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { Router } from '@angular/router';
import { EMPTY, Subject } from 'rxjs';
import { catchError, exhaustMap, finalize, map, tap } from 'rxjs/operators';
import { RestaurantService } from 'src/app/restaurants/services/restaurant.service';
import { AlertService } from 'src/app/shared/services/alert.service';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
import { AsyncValidatorsService } from 'src/app/shared/validators/async-validators.service';
import { matchingPasswords } from 'src/app/shared/validators/matching-passwords.validator';
import { RestaurantRegisterDto } from '../models/restaurant-register-dto';
import { RegisterErrorStateMatcher } from '../services/register-error-state-matcher';

@Component({
  selector: 'app-restaurant-register',
  templateUrl: './restaurant-register.component.html',
  styleUrls: ['./restaurant-register.component.scss'],
})
export class RestaurantRegisterComponent implements OnInit {
  registerForm: FormGroup;
  errorStateMatcher: ErrorStateMatcher;

  readonly minEmailLength = 5;
  readonly maxEmailLength = 100;
  readonly minPasswordLength = 5;
  readonly maxPasswordLength = 50;
  readonly minNameLength = 3;
  readonly maxNameLength = 100;

  private registerFormSubmitsSubject = new Subject<RestaurantRegisterDto>();

  constructor(
    private formBuilder: FormBuilder,
    private restaurantService: RestaurantService,
    private authenticationService: AuthenticationService,
    private alertService: AlertService,
    private router: Router,
    private asyncValidators: AsyncValidatorsService
  ) {}

  ngOnInit(): void {
    this.errorStateMatcher = new RegisterErrorStateMatcher();
    
    this.registerForm = this.formBuilder.group(
      {
        email: [
          null,
          {
            updateOn: 'blur',
            validators: [
              Validators.required,
              Validators.email,
              Validators.minLength(this.minEmailLength),
              Validators.maxLength(this.maxEmailLength),
            ],
            asyncValidators: [this.asyncValidators.checkIfDisposable()],
          },
        ],
        password: [
          null,
          [
            Validators.required,
            Validators.minLength(this.minPasswordLength),
            Validators.maxLength(this.maxPasswordLength),
          ],
        ],
        repeatPassword: [
          null,
          [
            Validators.required,
            Validators.minLength(this.minPasswordLength),
            Validators.maxLength(this.maxPasswordLength),
          ],
        ],
        name: [
          null,
          [
            Validators.required,
            Validators.pattern(/^(\p{L}| )+$/u),
            Validators.minLength(this.minNameLength),
            Validators.maxLength(this.maxNameLength),
          ],
        ],
        description: [null],
      },
      { validators: [matchingPasswords()] }
    );

    this.registerFormSubmitsSubject
      .pipe(
        tap({
          next: (_) => this.registerForm.disable(),
        }),
        map(restaurant => {
          delete (restaurant as any).repeatPassword;
          return restaurant;
        }),
        exhaustMap((restaurant) =>
          this.restaurantService.registerRestaurant(restaurant).pipe(
            catchError((error) => {
              this.alertService.displayMessage(
                error?.error?.description ||
                  'An error occurred while registering the user. Try again laer.',
                'error'
              );
              return EMPTY;
            }),
            finalize(() => this.registerForm.enable())
          )
        )
      )
      .subscribe((result) => {
        this.authenticationService.login(result.token);
        this.router.navigate(['restaurant', 'profile']);
      });
  }

  submit() {
    if (this.registerForm.valid) {
      this.registerFormSubmitsSubject.next(this.registerForm.value);
    }
  }
}
