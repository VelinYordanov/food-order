import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { EMPTY, of } from 'rxjs';
import { switchMap, map, catchError, tap, withLatestFrom, filter } from 'rxjs/operators';
import { AlertService } from 'src/app/shared/services/alert.service';
import { loggedInUserSelector } from 'src/app/store/authentication/authentication.selectors';
import { CustomerService } from '../../../customers/services/customer.service';
import { createAddressAction, createAddressErrorAction, createAddressSuccessAction, deleteAddressAction, deleteAddressErrorAction, deleteAddressPromptAction, deleteAddressSuccessAction, loadAddressesAction, loadAddressesErrorAction, loadAddressesSuccessAction, loadCustomerAddressAction, loadCustomerAddressErrorAction, loadCustomerAddressSuccessAction, updateAddressAction, updateAddressErrorAction, updateAddressSuccessAction } from './addresses.actions';

@Injectable()
export class AddressesEffects {
    constructor(
        private actions$: Actions,
        private customerService: CustomerService,
        private alertService: AlertService,
        private router: Router,
        private store: Store
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

    deleteAddressPrompts$ = createEffect(() =>
        this.actions$.pipe(
            ofType(deleteAddressPromptAction),
            switchMap(action => this.alertService.displayQuestionWithLoader(
                "Are you sure you want to delete this address?")
                .pipe(
                    filter(x => !!x),
                    map(x => action)
                )),
            map(action => deleteAddressAction({ payload: action.payload }))
        ))

    deleteAddresses$ = createEffect(() =>
        this.actions$.pipe(
            ofType(deleteAddressAction),
            withLatestFrom(this.store.select(loggedInUserSelector)),
            switchMap(([action, user]) => this.customerService.deleteCustomerAddress(user.id, action.payload.id)
                .pipe(
                    map(result => deleteAddressSuccessAction({ payload: result })),
                    catchError(error => of(deleteAddressErrorAction({ payload: error }))),
                ))
        ))

    deleteAddressesSuccess$ = createEffect(() =>
        this.actions$.pipe(
            ofType(deleteAddressSuccessAction),
            tap(({ payload }) => this.alertService.displayMessage(`Successfully deleted address ${payload.id}`, 'success'))
        ), { dispatch: false });

    deleteAddressesError$ = createEffect(() =>
        this.actions$.pipe(
            ofType(deleteAddressErrorAction),
            tap(({ payload }) => this.alertService.displayMessage(payload?.error?.description || `Error in deleting address. Try again later.`, 'error'))
        ), { dispatch: false })

    loadCustomerAddress$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loadCustomerAddressAction),
            switchMap(({ payload }) =>
                this.customerService.getCustomerAddress(payload.userId, payload.addressId)
                    .pipe(
                        map(result => loadCustomerAddressSuccessAction({ payload: result })),
                        catchError(error => of(loadCustomerAddressErrorAction({ payload: error })))
                    ))
        ));

    loadCustomerAddressErrors$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loadCustomerAddressErrorAction),
            tap(action => this.alertService.displayMessage('An error occurred while loading address. Try again later.', 'error'))
        ), { dispatch: false });

    updateAddress$ = createEffect(() =>
        this.actions$.pipe(
            ofType(updateAddressAction),
            switchMap(({ payload }) =>
                this.customerService.editCustomerAddress(payload.userId, payload.addressId, payload.address)
                    .pipe(
                        map(response => updateAddressSuccessAction({ payload: response })),
                        catchError(error => of(updateAddressErrorAction({ payload: error })))
                    ))
        ));

    updateAddressSuccess$ = createEffect(() =>
        this.actions$.pipe(
            ofType(updateAddressSuccessAction),
            tap(_ => {
                this.alertService.displayMessage('Successfully editted address.', 'success');
                this.router.navigate(['customer', 'profile']);
            })
        ), { dispatch: false });

    updateAddressError$ = createEffect(() =>
        this.actions$.pipe(
            ofType(updateAddressErrorAction),
            tap(({ payload }) => this.alertService.displayMessage(payload?.error?.description || 'An error occurred while editting address. Try again later.', 'error'))
        ), { dispatch: false });
}
