import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { of } from "rxjs";
import { catchError, map, switchMap, tap } from "rxjs/operators";
import { CustomerService } from "src/app/customers/services/customer.service";
import { AlertService } from "src/app/shared/services/alert.service";
import { loadCustomerOrdersAction, loadCustomerOrdersSuccessAction, loadCustomerOrdersErrorAction, loadOrderAction, loadOrderSuccessAction, loadOrderErrorAction } from "./customer-orders.actions";

@Injectable()
export class CustomerOrdersEffects {
    constructor(
        private actions$: Actions,
        private customerService: CustomerService,
        private alertService: AlertService
    ) { }

    loadCustomerOrders$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loadCustomerOrdersAction),
            switchMap(({ payload }) => this.customerService.getOrders(payload.customerId, payload.page)
                .pipe(
                    map(result => loadCustomerOrdersSuccessAction({ payload: result })),
                    catchError(error => of(loadCustomerOrdersErrorAction({ payload: error })))
                ))
        ));

    loadCustomerOrdersErrors$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loadCustomerOrdersErrorAction),
            tap(({ payload }) => this.alertService.displayMessage(
                payload?.error?.description ||
                'An error occurred while loading orders. Try again later.',
                'error'
            ))
        ), { dispatch: false });

    loadCustomerOrder$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loadOrderAction),
            switchMap(({ payload }) => this.customerService.getOrderById(payload.customerId, payload.customerId)
                .pipe(
                    map(result => loadOrderSuccessAction({ payload: result })),
                    catchError(error => of(loadOrderErrorAction({ payload: error })))
                ))
        ));

    loadCustomerOrderErrors$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loadOrderErrorAction),
            tap(({ payload }) => this.alertService.displayMessage(payload?.error?.description || 'An error occurred while loading offer. Try again later.', 'error'))
        ), { dispatch: false })
}