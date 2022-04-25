import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { EMPTY, of } from 'rxjs';
import { switchMap, map, catchError, tap } from 'rxjs/operators';
import { AlertService } from 'src/app/shared/services/alert.service';
import { CustomerService } from '../../services/customer.service';
import { createAddressAction, createAddressErrorAction, createAddressSuccessAction, loadAddressesAction, loadAddressesErrorAction, loadAddressesSuccessAction } from './addresses.actions';

@Injectable()
export class AddressesEffects {
    constructor(
        private actions$: Actions,
        private customerService: CustomerService,
        private alertService: AlertService,
        private router: Router
    ) { }

    loadAddresses$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loadAddressesAction),
            switchMap(action =>
                this.customerService.getCustomerAddresses(action.payload)
                    .pipe(
                        map(addresses => loadAddressesSuccessAction({ payload: addresses })),
                        catchError(error => of(loadAddressesErrorAction({ payload: error })))
                    )
            ))
    );

    loadAddressesErrors$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loadAddressesErrorAction),
            map(action => action.payload),
            tap(error => this.alertService.displayMessage(error?.error?.description || 'An error occurred while loading addresses. Try again later.', 'error')),
        ), { dispatch: false });

    createAddress$ = createEffect(() =>
        this.actions$.pipe(
            ofType(createAddressAction),
            map(action => action.payload),
            switchMap(({ customerId, address }) => this.customerService.addAddressToCustomer(customerId, address)
                .pipe(
                    map(address => createAddressSuccessAction({ payload: address })),
                    catchError(error => of(createAddressErrorAction({ payload: error })))
                ))
        ))

    createAddressErrors$ = createEffect(() =>
        this.actions$.pipe(
            ofType(createAddressErrorAction),
            map(action => action.payload),
            tap(error => this.alertService.displayMessage(error?.error?.description || 'An error occurred while adding address. Try again later.', 'error'))
        ), { dispatch: false });

    createAddressSuccesses$ = createEffect(() =>
        this.actions$.pipe(
            ofType(createAddressSuccessAction),
            tap(action => {
                this.alertService.displayMessage("Successfully added address!", 'success');
                this.router.navigate(['customer', 'profile']);
            })
        ), { dispatch: false });
}
