import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { Store } from "@ngrx/store";
import { of } from "rxjs";
import { catchError, filter, map, switchMap, tap, withLatestFrom } from "rxjs/operators";
import { RestaurantService } from "src/app/restaurants/services/restaurant.service";
import { AlertService } from "src/app/shared/services/alert.service";
import { loggedInUserIdSelector } from "../../authentication/authentication.selectors";
import { deleteDiscountCodeAction, deleteDiscountCodeErrorAction, deleteDiscountCodePromptAction, deleteDiscountCodeSuccessAction, editDiscountCodeAction, editDiscountCodeErrorAction, editDiscountCodeSuccessAction, loadDiscountCodesAction, loadDiscountCodesErrorAction, loadDiscountCodesSuccesAction } from "./discount-codes.actions";

@Injectable()
export class DiscountCodesEffects {
    constructor(
        private store: Store,
        private actions$: Actions,
        private restaurantService: RestaurantService,
        private alertService: AlertService
    ) { }

    loadDiscountCodes$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loadDiscountCodesAction),
            switchMap(action => this.restaurantService.getDiscountCodes(action.payload)
                .pipe(
                    map(discountCodes => loadDiscountCodesSuccesAction({ payload: discountCodes })),
                    catchError(error => of(loadDiscountCodesErrorAction({ payload: error })))
                ))
        ))

    loadDiscountCodesErrors$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loadDiscountCodesErrorAction),
            tap(({ payload }) => this.alertService.displayMessage(payload?.error?.description || 'An error occurred while loading discount codes. Try again later.', 'error'))
        ), { dispatch: false });

    discountCodeDeletePrompts$ = createEffect(() =>
        this.actions$.pipe(
            ofType(deleteDiscountCodePromptAction),
            switchMap(action => this.alertService
                .displayQuestion(
                    `Are you sure you want to delete discount code ${action.payload.code}?`
                ).pipe(
                    filter(x => !!x),
                    map(_ => deleteDiscountCodeAction({ payload: action.payload }))
                ))
        ))

    discountCodeDeletes$ = createEffect(() =>
        this.actions$.pipe(
            ofType(deleteDiscountCodeAction),
            withLatestFrom(this.store.select(loggedInUserIdSelector)),
            switchMap(([action, restaurantId]) => this.restaurantService.deleteDiscountCode(action.payload.id, restaurantId)
                .pipe(
                    map(data => deleteDiscountCodeSuccessAction({ payload: data })),
                    catchError(error => of(deleteDiscountCodeErrorAction({ payload: error })))
                ))
        ));

    discountCodeDeleteErrors$ = createEffect(() =>
        this.actions$.pipe(
            ofType(deleteDiscountCodeErrorAction),
            tap(({ payload }) => this.alertService.displayMessage(
                payload?.error?.description ||
                'An error occured while deleting discount code. Try again later.',
                'error'
            ))
        ), { dispatch: false });

    deleteDiscountCodeSuccesses$ = createEffect(() =>
        this.actions$.pipe(
            ofType(deleteDiscountCodeSuccessAction),
            tap(action => this.alertService.displayMessage(
                `Successfully deleted discount code ${action.payload.code}`,
                'success'
            ))
        ), { dispatch: false });

    editDiscountCode$ = createEffect(() =>
        this.actions$.pipe(
            ofType(editDiscountCodeAction),
            switchMap(({ payload }) => this.restaurantService.editDiscountCode(payload.discountCodeId, payload.restaurantId, payload.discountCode)
                .pipe(
                    map(discountCode => editDiscountCodeSuccessAction({ payload: discountCode })),
                    catchError(error => of(editDiscountCodeErrorAction({ payload: error })))
                ))
        ));

    editDiscountCodeErrors$ = createEffect(() =>
        this.actions$.pipe(
            ofType(editDiscountCodeErrorAction),
            tap(({ payload }) => this.alertService.displayMessage(
                payload?.error?.description ||
                'An error occurred while editting discount code. Try again later.',
                'error'
            ))
        ), { dispatch: false });

    editDiscountCodeSuccesses$ = createEffect(() =>
        this.actions$.pipe(
            ofType(editDiscountCodeSuccessAction),
            tap(({ payload }) => this.alertService.displayMessage(
                `Successfully editted discount code ${payload.code}`,
                'success'
            ))
        ), { dispatch: false });
}