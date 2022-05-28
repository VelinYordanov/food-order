import { Injectable } from "@angular/core";
import { Router } from "@angular/router";
import { JwtHelperService } from "@auth0/angular-jwt";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { of } from "rxjs";
import { catchError, filter, map, switchMap, tap } from "rxjs/operators";
import { CustomerService } from "src/app/customers/services/customer.service";
import { LoginService } from "src/app/home/services/login-service.service";
import { RestaurantService } from "src/app/restaurants/services/restaurant.service";
import { AlertService } from "../../shared/services/alert.service";
import { StorageService } from "../../shared/services/storage.service";
import { loginCustomerAction, loginCustomerErrorAction, loginCustomerSuccessAction, loginRestaurantAction, loginRestaurantErrorAction, loginRestaurantSuccessAction, registerCustomerAction, registerCustomerErrorAction, registerCustomerSuccessAction, registerRestaurantAction, registerRestaurantErrorAction, registerRestaurantSuccessAction, updateUserAction } from "./authentication.actions";

@Injectable()
export class AuthenticationEffects {
    constructor(
        private router: Router,
        private actions$: Actions,
        private loginService: LoginService,
        private storageService: StorageService,
        private jwtService: JwtHelperService,
        private alertService: AlertService,
        private customerService: CustomerService,
        private restaurantService: RestaurantService
    ) { }

    customerLogins$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loginCustomerAction),
            switchMap(action =>
                this.loginService.loginCustomer(action.payload)
                    .pipe(
                        map(response => loginCustomerSuccessAction({ payload: response })),
                        catchError(error => of(loginCustomerErrorAction({ payload: error })))
                    ))
        ))

    customerLoginSuccesses$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loginCustomerSuccessAction),
            tap(_ => this.router.navigate(['customer', 'profile']))
        ), { dispatch: false });

    restaurantLogins$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loginRestaurantAction),
            switchMap(action =>
                this.loginService.loginRestaurant(action.payload)
                    .pipe(
                        map(response => loginRestaurantSuccessAction({ payload: response })),
                        catchError(error => of(loginCustomerErrorAction({ payload: error })))
                    ))
        ))

    restaurantLoginSuccesses$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loginRestaurantSuccessAction),
            tap(_ => this.router.navigate(['restaurant', 'profile']))
        ), { dispatch: false });

    successfulLogins$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loginCustomerSuccessAction, loginRestaurantSuccessAction),
            map(action => this.login(action?.payload?.token))
        ))

    loginErrors$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loginCustomerErrorAction, loginRestaurantErrorAction),
            map(action => action.payload),
            tap(error => this.alertService.displayMessage(error?.error?.description || 'An error occurred while logging in. Try again later.', 'error'))
        ), { dispatch: false });

    registerCustomer$ = createEffect(() =>
        this.actions$.pipe(
            ofType(registerCustomerAction),
            switchMap(action => this.customerService.registerCustomer(action.payload)
                .pipe(
                    map(result => registerCustomerSuccessAction({ payload: result })),
                    catchError(error => of(registerCustomerErrorAction({ payload: error })))
                ))
        ));

    registerCustomerSuccesses$ = createEffect(() =>
        this.actions$.pipe(
            ofType(registerCustomerSuccessAction),
            map(action => this.login(action.payload.token))
        ));

    registerCustomerErrors$ = createEffect(() =>
        this.actions$.pipe(
            ofType(registerCustomerErrorAction),
            tap(({ payload }) => this.alertService.displayMessage(
                payload?.error?.description ||
                'An error occurred while registering the user. Try again later.',
                'error'
            ))
        ), { dispatch: false });

    registerRestaurant$ = createEffect(() =>
        this.actions$.pipe(
            ofType(registerRestaurantAction),
            switchMap(action => this.restaurantService.registerRestaurant(action.payload)
                .pipe(
                    map(result => registerRestaurantSuccessAction({ payload: result })),
                    catchError(error => of(registerRestaurantErrorAction({ payload: error })))
                ))
        ));

    registerRestaurantSuccesses$ = createEffect(() =>
        this.actions$.pipe(
            ofType(registerRestaurantSuccessAction),
            map(action => this.login(action.payload.token))
        ));

    registerRestaurantErrors$ = createEffect(() =>
        this.actions$.pipe(
            ofType(registerRestaurantErrorAction),
            tap(({ payload }) => this.alertService.displayMessage(
                payload?.error?.description ||
                'An error occurred while registering the user. Try again later.',
                'error'
            ))
        ), { dispatch: false });

    logouts$ = createEffect(() =>
        this.actions$.pipe(
            ofType(updateUserAction),
            filter(x => !x.payload),
            tap(action => this.storageService.removeItem('jwt-user'))
        ), { dispatch: false });

    private login(token: string) {
        const user = this.jwtService.decodeToken(token);
        this.storageService.setItem('jwt-user', token);
        return updateUserAction({ payload: user });
    }
}