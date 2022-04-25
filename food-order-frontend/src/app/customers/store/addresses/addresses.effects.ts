import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { EMPTY, of } from 'rxjs';
import { switchMap, map, catchError, tap } from 'rxjs/operators';
import { AlertService } from 'src/app/shared/services/alert.service';
import { CustomerService } from '../../services/customer.service';
import { loadAddressesAction, loadAddressesErrorAction, loadAddressesSuccessAction } from './addresses.actions';

@Injectable()
export class AddressesEffects {
    constructor(
        private actions$: Actions,
        private customerService: CustomerService,
        private alertService: AlertService
    ) { }

    loadAddresses$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loadAddressesAction),
            switchMap(action =>
                this.customerService.getCustomerAddresses(action.customerId)
                    .pipe(
                        map(addresses => loadAddressesSuccessAction({ addresses })),
                        catchError(error => of(loadAddressesErrorAction({ error })))
                    )
            ))
    );

    loadAddressesErrors$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loadAddressesErrorAction),
            map(action => action.error),
            tap(error => this.alertService.displayMessage(error?.error?.description || 'An error occurred while loading addresses. Try again later.', 'error')),
        ), { dispatch: false });
}
