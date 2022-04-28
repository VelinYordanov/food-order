import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { Store } from "@ngrx/store";
import { of } from "rxjs";
import { catchError, filter, map, switchMap, tap, withLatestFrom } from "rxjs/operators";
import { EnumsService } from "src/app/shared/services/enums.service";
import { loadAddressTypesAction, loadAddressTypesErrorAction, loadAddressTypesSuccessAction, loadCitiesAction, loadCitiesErrorAction, loadCitiesSuccessAction, loadOrderStatusesAction, loadOrderStatusesErrorAction, loadOrderStatusesSuccessAction } from "./enums.actions";
import { addressTypesSelector, citiesSelector, orderTypesSelector } from "./enums.selectors";

@Injectable()
export class EnumEffects {
    constructor(
        private store: Store,
        private actions$: Actions,
        private enumsService: EnumsService
    ) { }

    loadCities$ = createEffect(() =>
        this.actions$
            .pipe(
                ofType(loadCitiesAction),
                withLatestFrom(this.store.select(citiesSelector)),
                filter(([_, cities]) => !cities.length),
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
                withLatestFrom(this.store.select(addressTypesSelector)),
                filter(([_, addressTypes]) => !addressTypes.length),
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
                withLatestFrom(this.store.select(orderTypesSelector)),
                filter(([_, orderStatuses]) => !orderStatuses.length),
                switchMap(action =>
                    this.enumsService.getOrderStatuses()
                        .pipe(
                            map(orderStatuses => loadOrderStatusesSuccessAction({ payload: orderStatuses })),
                            catchError(error => of(loadOrderStatusesErrorAction(error)))
                        ))
            ));
}