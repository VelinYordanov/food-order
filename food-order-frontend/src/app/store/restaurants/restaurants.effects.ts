import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { of } from "rxjs";
import { catchError, map, switchMap, tap } from "rxjs/operators";
import { RestaurantService } from "src/app/restaurants/services/restaurant.service";
import { AlertService } from "src/app/shared/services/alert.service";
import { loadRestaurantAction, loadRestaurantErrorAction, loadRestaurantsAction, loadRestaurantsErrorAction, loadRestaurantsSuccessAction, loadRestaurantSuccessAction } from "./restaurants.actions";

@Injectable()
export class RestaurantsEffects {
    constructor(
        private restaurantService: RestaurantService,
        private alertService: AlertService,
        private actions$: Actions
    ) { }

    loadRestaurants$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loadRestaurantsAction),
            switchMap(action => this.restaurantService.getRestaurantsList()
                .pipe(
                    map(data => loadRestaurantsSuccessAction({ payload: data })),
                    catchError(error => of(loadRestaurantsErrorAction({ payload: error })))
                ))
        ));

    loadRestaurantsErrors$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loadRestaurantsErrorAction),
            tap(({ payload }) => this.alertService.displayMessage(payload?.error?.desccription || 'An error occurred while loading restaurants data. Try again later.', 'error'))
        ));

    loadRestaurant$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loadRestaurantAction),
            switchMap(action => this.restaurantService.getRestaurantData(action.payload)
                .pipe(
                    map(data => loadRestaurantSuccessAction({ payload: data })),
                    catchError(error => of(loadRestaurantErrorAction({ payload: error })))
                ))
        ));

    loadRestaurantErrors$ = createEffect(() =>
        this.actions$.pipe(
            ofType(loadRestaurantErrorAction),
            tap(({ payload }) => this.alertService.displayMessage(payload?.error?.description || 'An error ocurred while loading restaurant data. Try again later.', 'error'))
        ));
}