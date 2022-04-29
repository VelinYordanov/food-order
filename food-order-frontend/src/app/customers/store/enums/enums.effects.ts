import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { Store } from "@ngrx/store";
import { of } from "rxjs";
import { catchError, filter, map, switchMap, tap, withLatestFrom } from "rxjs/operators";
import { EnumsService } from "src/app/shared/services/enums.service";
import { loadAddressTypesAction, loadAddressTypesErrorAction, loadAddressTypesRequestAction, loadAddressTypesSuccessAction, loadCitiesAction, loadCitiesErrorAction, loadCitiesRequestAction, loadCitiesSuccessAction, loadOrderStatusesAction, loadOrderStatusesErrorAction, loadOrderStatusesSuccessAction } from "./enums.actions";
import { addressTypesEnumStoreDataSelector, addressTypesSelector, citiesEnumStoreDataSelector, citiesSelector, orderTypesEnumStoreDataSelector, orderTypesSelector } from "./enums.selectors";

@Injectable()
export class EnumEffects {
    constructor(
        private store: Store,
        private actions$: Actions,
        private enumsService: EnumsService
    ) { }

    loadCitiesRequests$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loadCitiesAction),
            withLatestFrom(this.store.select(citiesEnumStoreDataSelector)),
            filter(([_, cities]) => !cities.entities.length && !cities.isLoading),
            map(x => loadCitiesRequestAction())
        )
    )

    loadCities$ = createEffect(() =>
        this.actions$
            .pipe(
                ofType(loadCitiesRequestAction),
                switchMap(action =>
                    this.enumsService.getCities()
                        .pipe(
                            map(cities => loadCitiesSuccessAction({ payload: { entities: cities, isLoading: false } })),
                            catchError(error => of(loadCitiesErrorAction(error)))
                        ))
            ));

    loadAddressTypesRequests$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loadAddressTypesRequestAction),
            withLatestFrom(this.store.select(addressTypesEnumStoreDataSelector)),
            filter(([_, addressTypes]) => !addressTypes.entities.length && !addressTypes.isLoading),
            map(x => loadAddressTypesRequestAction())
        )
    )

    loadAddressTypes$ = createEffect(() =>
        this.actions$
            .pipe(
                ofType(loadAddressTypesAction),
                withLatestFrom(this.store.select(addressTypesEnumStoreDataSelector)),
                filter(([_, addressTypes]) => !addressTypes.entities.length && !addressTypes.isLoading),
                switchMap(action =>
                    this.enumsService.getAddressTypes()
                        .pipe(
                            map(addressTypes => loadAddressTypesSuccessAction({ payload: { entities: addressTypes, isLoading: false } })),
                            catchError(error => of(loadAddressTypesErrorAction(error)))
                        ))
            ));

    loadOrderStatuses$ = createEffect(() =>
        this.actions$
            .pipe(
                ofType(loadOrderStatusesAction),
                withLatestFrom(this.store.select(orderTypesEnumStoreDataSelector)),
                filter(([_, orderStatuses]) => !orderStatuses.entities.length && !orderStatuses.isLoading),
                switchMap(action =>
                    this.enumsService.getOrderStatuses()
                        .pipe(
                            map(orderStatuses => loadOrderStatusesSuccessAction({ payload: { entities: orderStatuses, isLoading: false } })),
                            catchError(error => of(loadOrderStatusesErrorAction(error)))
                        ))
            ));
}