import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { of } from "rxjs";
import { catchError, filter, map, switchMap, tap } from "rxjs/operators";
import { RestaurantService } from "src/app/restaurants/services/restaurant.service";
import { AlertService } from "src/app/shared/services/alert.service";
import { addCategoryToRestaurantAction, addCategoryToRestaurantErrorAction, addCategoryToRestaurantPromptAction, addCategoryToRestaurantSuccessAction, addFoodToRestaurantAction, addFoodToRestaurantErrorAction, addFoodToRestaurantSuccessAction, loadRestaurantAction, loadRestaurantErrorAction, loadRestaurantsAction, loadRestaurantsErrorAction, loadRestaurantsSuccessAction, loadRestaurantSuccessAction } from "./restaurants.actions";

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
        ), { dispatch: false });

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
        ), { dispatch: false });

    addCategoryToRestaurantPrompt$ = createEffect(() =>
        this.actions$.pipe(
            ofType(addCategoryToRestaurantPromptAction),
            switchMap(action => this.alertService.displayQuestion(`Category ${action.payload.categoryName} does not exist. Do you want to create it?`).pipe(
                filter(x => !!x),
                map(_ => addCategoryToRestaurantAction({ payload: action.payload }))
            ))
        ))

    addCategoryToRestaurant$ = createEffect(() =>
        this.actions$.pipe(
            ofType(addCategoryToRestaurantAction),
            switchMap(action => this.restaurantService.addCategoryToRestaurant(action.payload.restaurantId, action.payload.categoryName)
                .pipe(
                    map(result => addCategoryToRestaurantSuccessAction({ payload: result })),
                    catchError(error => of(addCategoryToRestaurantErrorAction({ payload: error })))
                ))
        ))

    addCategoryToRestaurantErrors$ = createEffect(() =>
        this.actions$.pipe(
            ofType(addCategoryToRestaurantErrorAction),
            tap(({ payload }) => this.alertService.displayMessage(payload?.error?.description || 'An error occurred while creating category.', 'error'))
        ), { dispatch: false });

    addFoodToRestaurant$ = createEffect(() =>
        this.actions$.pipe(
            ofType(addFoodToRestaurantAction),
            switchMap(action => this.restaurantService.addFood(action.payload.restaurantId, action.payload.food)
                .pipe(
                    map(result => addFoodToRestaurantSuccessAction({ payload: result })),
                    catchError(error => of(addFoodToRestaurantErrorAction({ payload: error })))
                ))
        ))

    addFoodToRestaurantErrors$ = createEffect(() =>
        this.actions$.pipe(
            ofType(addFoodToRestaurantErrorAction),
            tap(({ payload }) => this.alertService.displayMessage(payload?.error?.description || 'An error occurred while adding food. Try again later.', 'error'))
        ), { dispatch: false });

    addFoodToRestaurantSuccess$ = createEffect(() =>
        this.actions$.pipe(
            ofType(addFoodToRestaurantSuccessAction),
            tap(({ payload }) => this.alertService.displayMessage(`Successfully added food ${payload.name}`, 'success'))
        ), { dispatch: false });
}