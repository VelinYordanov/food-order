import { Injectable } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { of } from "rxjs";
import { catchError, map, switchMap, tap } from "rxjs/operators";
import { CustomerService } from "src/app/customers/services/customer.service";
import { AlertService } from "src/app/shared/services/alert.service";
import { clearCartAction, loadDiscountCodeAction, loadDiscountCodeErrorAction, loadDiscountCodeSuccessAction, submitOrderAction, submitOrderErrorAction, submitOrderSuccessAction } from "./cart.actions";

@Injectable()
export class CartEffects {
    constructor(
        private actions$: Actions,
        private alertService: AlertService,
        private router: Router,
        private activatedRoute: ActivatedRoute,
        private customerService: CustomerService) { }

    loadDiscountCode$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loadDiscountCodeAction),
            switchMap(action =>
                this.customerService.getDiscountCode(action.payload.restaurantId, action.payload.code)
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
        ));

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
                this.router.navigate(['../', action.payload.id], {
                    relativeTo: this.activatedRoute,
                });
            })
        ), { dispatch: false })

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

    submitOrderSuccessesAndErrors$ = createEffect(() =>
        this.actions$.pipe(
            ofType(submitOrderSuccessAction, submitOrderErrorAction),
            map(action => clearCartAction()),
        ))
}