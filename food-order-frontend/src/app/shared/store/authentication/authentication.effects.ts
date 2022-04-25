import { JwtHelperService } from "@auth0/angular-jwt";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { of } from "rxjs";
import { catchError, map, switchMap, tap } from "rxjs/operators";
import { LoginService } from "src/app/home/services/login-service.service";
import { StorageService } from "../../services/storage.service";
import { loginCustomerAction, loginCustomerErrorAction, loginCustomerSuccessAction, loginRestaurantAction, loginRestaurantSuccessAction, updateUserAction } from "./authentication.actions";

export class AuthenticationEffects {
    constructor(
        private actions$: Actions,
        private loginService: LoginService,
        private storageService: StorageService,
        private jwtService: JwtHelperService
    ) { }

    customerLogins$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loginCustomerAction),
            switchMap(action =>
                this.loginService.loginCustomer(action.loginData)
                    .pipe(
                        map(response => loginCustomerSuccessAction({ jwtToken: response })),
                        catchError(error => of(loginCustomerErrorAction({ error })))
                    ))
        ))

    restaurantLogins$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loginRestaurantAction),
            switchMap(action =>
                this.loginService.loginRestaurant(action.loginData)
                    .pipe(
                        map(response => loginRestaurantSuccessAction({ jwtToken: response })),
                        catchError(error => of(loginCustomerErrorAction({ error })))
                    ))
        ))

    successfulLogins$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loginCustomerSuccessAction, loginRestaurantSuccessAction),
            map(jwtToken => this.login(jwtToken?.jwtToken?.token))
        ))

    private login(token: string) {
        const user = this.jwtService.decodeToken(token);
        this.storageService.setItem('jwt-user', token);
        return updateUserAction({ user });
    }
}