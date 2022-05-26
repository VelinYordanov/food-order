import { Injectable } from "@angular/core";
import { Router } from "@angular/router";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { Store } from "@ngrx/store";
import { of } from "rxjs";
import { catchError, map, switchMap, tap, withLatestFrom } from "rxjs/operators";
import { CustomerService } from "src/app/customers/services/customer.service";
import { AlertService } from "src/app/shared/services/alert.service";
import { loggedInUserIdSelector } from "../../authentication/authentication.selectors";
import { clearCartAction, loadDiscountCodeAction, loadDiscountCodeErrorAction, loadDiscountCodeSuccessAction, submitOrderAction, submitOrderErrorAction, submitOrderSuccessAction } from "./cart.actions";
import { selectedRestaurantIdSelector } from "./cart.selectors";

@Injectable()
export class CartEffects {
    constructor(
        private actions$: Actions,
        private store: Store,
        private alertService: AlertService,
        private router: Router,
        private customerService: CustomerService) { }

    loadDiscountCode$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loadDiscountCodeAction),
            withLatestFrom(this.store.select(selectedRestaurantIdSelector)),
            switchMap(([action, restaurantId]) =>
                this.customerService.getDiscountCode(restaurantId, action.payload)
                    .pipe(
                        map(result => loadDiscountCodeSuccessAction({ payload: result })),
                        catchError(error => of(loadDiscountCodeErrorAction({ payload: error })))
                    ))
        )
    )

    loadDiscountCodeErrors$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loadDiscountCodeErrorAction),
            tap(({ payload }) => this.alertService.displayMessage(
                payload?.error?.description ||
                'An error occurred while looking up discount code. Try again later.',
                'error'
            ))
        ), { dispatch: false });

    submitOrder$ = createEffect(() =>
        this.actions$.pipe(
            ofType(submitOrderAction),
            switchMap(action => this.customerService.submitOrder(action.payload)
                .pipe(
                    map(result => submitOrderSuccessAction({ payload: result })),
                    catchError(error => of(submitOrderErrorAction({ payload: error })))
                ))
        ))

    submitOrderSuccess$ = createEffect(() =>
        this.actions$.pipe(
            ofType(submitOrderSuccessAction),
            tap(action => {
                this.alertService.displayMessage(
                    'Successfully submitted order.',
                    'success'
                );

                this.router.navigate(['/customer/order/', action.payload.id]);
            }),
            map(action => clearCartAction())
        ))

    submitOrderErrors$ = createEffect(() =>
        this.actions$.pipe(
            ofType(submitOrderErrorAction),
            tap(action => {
                this.alertService.displayMessage(
                    action.payload?.error?.description ||
                    'An error occurred while submitting order. Try again later',
                    'error'
                );
            })
        ), { dispatch: false })
}