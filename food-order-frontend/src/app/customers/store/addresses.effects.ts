import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { switchMap, map, catchError, tap } from 'rxjs/operators';
import { CustomerService } from '../services/customer.service';
import { loadAddressesAction, loadAddressesError, loadAddressesSuccess } from './customers.actions';

@Injectable()
export class AddressesEffects {
    constructor(private actions$: Actions, private customerService: CustomerService) { }

    loadProducts$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loadAddressesAction),
            tap(console.log),
            switchMap(action =>
                this.customerService.getCustomerAddresses(action.customerId)
                    .pipe(
                        map(addresses => loadAddressesSuccess({ addresses })),
                        catchError(error => of(loadAddressesError({ error })))
                    )
            ))
    );
}
