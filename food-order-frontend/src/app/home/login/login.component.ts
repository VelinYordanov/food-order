import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Subject } from 'rxjs';
import { tap, takeUntil } from 'rxjs/operators';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { loginCustomerAction, loginCustomerErrorAction, loginCustomerSuccessAction, loginRestaurantAction, loginRestaurantErrorAction, loginRestaurantSuccessAction } from 'src/app/store/authentication/authentication.actions';
import { Actions, ofType } from '@ngrx/effects';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit, OnDestroy {
  loginForm: FormGroup;
  minEmailLength = 5;
  maxEmailLength = 100;
  minPasswordLength = 5;
  maxPasswordLength = 50;

  private loginFormSubmitsSubject = new Subject<any>();
  private onDestroy$ = new Subject<any>();

  constructor(
    private formBuilder: FormBuilder,
    private store: Store,
    private actions: Actions
  ) { }

  ngOnInit(): void {
    this.loginFormSubmitsSubject
      .pipe(
        tap(_ => this.loginForm.disable()),
      ).subscribe(data => {
        data.isRestaurant ?
          this.store.dispatch(loginRestaurantAction({ payload: data })) :
          this.store.dispatch(loginCustomerAction({ payload: data }));
      });

    this.loginForm = this.formBuilder.group({
      "email": [null, [Validators.required, Validators.email, Validators.minLength(this.minEmailLength), Validators.maxLength(this.maxEmailLength)]],
      "password": [null, [Validators.required, Validators.minLength(this.minPasswordLength), Validators.maxLength(this.maxPasswordLength)]],
      "isRestaurant": [false],
    })

    this.actions.pipe(
      takeUntil(this.onDestroy$),
      ofType(loginCustomerSuccessAction, loginCustomerErrorAction, loginRestaurantSuccessAction, loginRestaurantErrorAction),
    ).subscribe(_ => this.loginForm.enable());
  }

  ngOnDestroy(): void {
    this.onDestroy$.next();
    this.onDestroy$.complete();
  }

  submit() {
    if (this.loginForm.valid) {
      this.loginFormSubmitsSubject.next(this.loginForm.value);
    }
  }
}
