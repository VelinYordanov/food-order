import { Injectable } from "@angular/core";
import { JwtHelperService } from "@auth0/angular-jwt";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { Store } from "@ngrx/store";
import { of } from "rxjs";
import { catchError, map, switchMap, tap } from "rxjs/operators";
import { deleteAddressPromptAction } from "src/app/customers/store/addresses/addresses.actions";
import { LoginService } from "src/app/home/services/login-service.service";
import { AlertService } from "../../services/alert.service";
import { StorageService } from "../../services/storage.service";
import { loginCustomerAction, loginCustomerErrorAction, loginCustomerSuccessAction, loginRestaurantAction, loginRestaurantErrorAction, loginRestaurantSuccessAction, updateUserAction } from "./authentication.actions";

@Injectable()
export class AuthenticationEffects {
    constructor(
        private actions$: Actions,
        private loginService: LoginService,
        private storageService: StorageService,
        private jwtService: JwtHelperService,
        private alertService: AlertService,
        private store: Store
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

    private login(token: string) {
        const user = this.jwtService.decodeToken(token);
        this.storageService.setItem('jwt-user', token);
        return updateUserAction({ payload: user });
    }
}