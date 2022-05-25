import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { of } from "rxjs";
import { catchError, map, switchMap, tap } from "rxjs/operators";
import { RestaurantService } from "src/app/restaurants/services/restaurant.service";
import { AlertService } from "src/app/shared/services/alert.service";
import { loadMonthlyGraphAction, loadMonthlyGraphErrorAction, loadMonthlyGraphSuccesAction, loadYearlyGraphAction, loadYearlyGraphErrorAction, loadYearlyGraphSuccesAction } from "./graphs.actions";

@Injectable()
export class GraphsEffects {
    constructor(
        private restaurantService: RestaurantService,
        private alertService: AlertService,
        private actions$: Actions
    ) { }

    loadMonthyGraphData$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loadMonthlyGraphAction),
            switchMap(action => this.restaurantService.getMonthyGraphData(action.payload.restaurantId, action.payload.month, action.payload.year)
                .pipe(
                    map(result => loadMonthlyGraphSuccesAction({ payload: result })),
                    catchError(error => of(loadMonthlyGraphErrorAction({ payload: error })))
                ))
        ));

    loadMonthlyGraphDataErrors$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loadMonthlyGraphErrorAction),
            tap(({ payload }) => this.alertService.displayMessage(
                payload?.error?.description ||
                'An error occurred while loading monthly graph. Try again later.',
                'error'
            ))
        ), { dispatch: false })

    loadYearlyGraphData$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loadYearlyGraphAction),
            switchMap(action => this.restaurantService.getYearlyGraph(action.payload.restaurantId, action.payload.year)
                .pipe(
                    map(result => loadYearlyGraphSuccesAction({ payload: { graphData: result, year: action.payload.year} })),
                    catchError(error => of(loadYearlyGraphErrorAction({ payload: error })))
                ))
        ));

    loadYearlyGraphDataErrors$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loadYearlyGraphErrorAction),
            tap(({ payload }) => this.alertService.displayMessage(
                payload?.error?.description ||
                'An error occurred while loading yearly graph. Try again later.',
                'error'
            ))
        ), { dispatch: false })
}