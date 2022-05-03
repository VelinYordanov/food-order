import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { Actions, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { Subject } from 'rxjs';
import { map, takeUntil, tap } from 'rxjs/operators';
import { AsyncValidatorsService } from 'src/app/shared/validators/async-validators.service';
import { matchingPasswords } from 'src/app/shared/validators/matching-passwords.validator';
import { registerRestaurantAction, registerRestaurantErrorAction, registerRestaurantSuccessAction } from 'src/app/store/authentication/authentication.actions';
import { RestaurantRegisterDto } from '../models/restaurant-register-dto';
import { RegisterErrorStateMatcher } from '../services/register-error-state-matcher';

@Component({
  selector: 'app-restaurant-register',
  templateUrl: './restaurant-register.component.html',
  styleUrls: ['./restaurant-register.component.scss'],
})
export class RestaurantRegisterComponent implements OnInit, OnDestroy {
  registerForm: FormGroup;
  errorStateMatcher: ErrorStateMatcher;

  readonly minEmailLength = 5;
  readonly maxEmailLength = 100;
  readonly minPasswordLength = 5;
  readonly maxPasswordLength = 50;
  readonly minNameLength = 3;
  readonly maxNameLength = 100;

  private registerFormSubmitsSubject = new Subject<RestaurantRegisterDto>();
  private onDestroy$ = new Subject<void>();

  constructor(
    private store: Store,
    private actions$: Actions,
    private formBuilder: FormBuilder,
    private asyncValidators: AsyncValidatorsService
  ) { }

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
        tap(_ => this.registerForm.disable()),
        map(restaurant => {
          delete (restaurant as any).repeatPassword;
          return restaurant;
        })
      )
      .subscribe(data => {
        this.store.next(registerRestaurantAction({ payload: data }));
      });

    this.actions$.pipe(
      takeUntil(this.onDestroy$),
      ofType(registerRestaurantSuccessAction, registerRestaurantErrorAction)
    ).subscribe(_ => this.registerForm.enable());
  }

  ngOnDestroy(): void {
    this.onDestroy$.next();
    this.onDestroy$.complete();
    this.registerFormSubmitsSubject.complete();
  }

  submit() {
    if (this.registerForm.valid) {
      this.registerFormSubmitsSubject.next(this.registerForm.value);
    }
  }
}
