import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { of } from "rxjs";
import { catchError, map, switchMap, tap } from "rxjs/operators";
import { EnumsService } from "src/app/shared/services/enums.service";
import { loadAddressTypesAction, loadAddressTypesErrorAction, loadAddressTypesSuccessAction, loadCitiesAction, loadCitiesErrorAction, loadCitiesSuccessAction, loadOrderStatusesAction, loadOrderStatusesErrorAction, loadOrderStatusesSuccessAction } from "./enums.actions";

@Injectable()
export class EnumEffects {
    constructor(
        private actions$: Actions,
        private enumsService: EnumsService
    ) { }

    loadCities$ = createEffect(() =>
        this.actions$
            .pipe(
                ofType(loadCitiesAction),
                switchMap(action =>
                    this.enumsService.getCities()
                        .pipe(
                            map(cities => loadCitiesSuccessAction({ payload: cities })),
                            catchError(error => of(loadCitiesErrorAction(error)))
                        ))
            ));

    loadAddressTypes$ = createEffect(() =>
        this.actions$
            .pipe(
                ofType(loadAddressTypesAction),
                switchMap(action =>
                    this.enumsService.getAddressTypes()
                        .pipe(
                            map(addressTypes => loadAddressTypesSuccessAction({ payload: addressTypes })),
                            catchError(error => of(loadAddressTypesErrorAction(error)))
                        ))
            ));

    loadOrderStatuses$ = createEffect(() =>
        this.actions$
            .pipe(
                ofType(loadOrderStatusesAction),
                switchMap(action =>
                    this.enumsService.getOrderStatuses()
                        .pipe(
                            map(orderStatuses => loadOrderStatusesSuccessAction({ payload: orderStatuses })),
                            catchError(error => of(loadOrderStatusesErrorAction(error)))
                        ))
            ));
}