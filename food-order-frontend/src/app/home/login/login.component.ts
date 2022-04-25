import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Subject, EMPTY } from 'rxjs';
import { LoginService } from '../services/login-service.service';
import { exhaustMap, catchError, tap, switchMap, takeUntil } from 'rxjs/operators';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
import { Router } from '@angular/router';
import { AlertService } from 'src/app/shared/services/alert.service';
import { Store } from '@ngrx/store';
import { loginCustomerAction, loginCustomerErrorAction, loginCustomerSuccessAction, loginRestaurantAction, loginRestaurantErrorAction, loginRestaurantSuccessAction } from 'src/app/shared/store/authentication/authentication.actions';
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
    private router: Router,
    private actions: Actions
  ) { }

  ngOnInit(): void {
    this.loginFormSubmitsSubject
      .pipe(
        tap(_ => this.loginForm.disable()),
      ).subscribe(data => {
        data.isRestaurant ?
          this.store.dispatch(loginRestaurantAction({ loginData: data })) :
          this.store.dispatch(loginCustomerAction({ loginData: data }));
      });

    this.loginForm = this.formBuilder.group({
      "email": [null, [Validators.required, Validators.email, Validators.minLength(this.minEmailLength), Validators.maxLength(this.maxEmailLength)]],
      "password": [null, [Validators.required, Validators.minLength(this.minPasswordLength), Validators.maxLength(this.maxPasswordLength)]],
      "isRestaurant": [false],
    })

    this.actions.pipe(
      takeUntil(this.onDestroy$),
      ofType(loginCustomerSuccessAction, loginCustomerErrorAction, loginRestaurantSuccessAction, loginRestaurantErrorAction),
      tap(_ => this.loginForm.enable()),
      switchMap(x => EMPTY)
    ).subscribe();

    this.actions.pipe(
      takeUntil(this.onDestroy$),
      ofType(loginCustomerSuccessAction, loginRestaurantSuccessAction),
      tap(_ => {
        if (this.loginForm.get('isRestaurant').value) {
          this.router.navigate(['restaurant', 'profile']);
        } else {
          this.router.navigate(['customer', 'profile']);
        }
      }),
      switchMap(x => EMPTY)
    ).subscribe()
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
