import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { Store } from "@ngrx/store";
import { of } from "rxjs";
import { catchError, filter, map, switchMap, tap, withLatestFrom } from "rxjs/operators";
import { RestaurantService } from "src/app/restaurants/services/restaurant.service";
import { AlertService } from "src/app/shared/services/alert.service";
import { loggedInUserIdSelector } from "../authentication/authentication.selectors";
import { addCategoryToRestaurantAction, addCategoryToRestaurantErrorAction, addCategoryToRestaurantPromptAction, addCategoryToRestaurantSuccessAction, addFoodToRestaurantAction, addFoodToRestaurantErrorAction, addFoodToRestaurantSuccessAction, deleteCategoryFromRestaurantAction, deleteCategoryFromRestaurantErrorAction, deleteCategoryFromRestaurantPromptAction, deleteCategoryFromRestaurantSuccessAction, editRestaurantAction, editRestaurantErrorAction, editRestaurantFoodAction, editRestaurantFoodErrorAction, editRestaurantFoodSuccessAction, editRestaurantSuccessAction, loadRestaurantAction, loadRestaurantErrorAction, loadRestaurantsAction, loadRestaurantsErrorAction, loadRestaurantsSuccessAction, loadRestaurantSuccessAction } from "./restaurants.actions";

@Injectable()
export class RestaurantsEffects {
    constructor(
        private restaurantService: RestaurantService,
        private alertService: AlertService,
        private actions$: Actions,
        private store: Store
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
            switchMap(action => this.alertService.displayQuestion(action.payload.promptQuestion).pipe(
                filter(x => !!x),
                map(_ => addCategoryToRestaurantAction({ payload: action.payload }))
            ))
        ))

    addCategoryToRestaurant$ = createEffect(() =>
        this.actions$.pipe(
            ofType(addCategoryToRestaurantAction),
            switchMap(action => this.restaurantService.addCategoryToRestaurant(action.payload.data.restaurantId, action.payload.data.categoryName)
                .pipe(
                    map(result => addCategoryToRestaurantSuccessAction({ payload: { successText: action.payload.successText, data: result } })),
                    catchError(error => of(addCategoryToRestaurantErrorAction({ payload: { error, errorText: action.payload.errorText } })))
                ))
        ))

    addCategoryToRestaurantErrors$ = createEffect(() =>
        this.actions$.pipe(
            ofType(addCategoryToRestaurantErrorAction),
            tap(({ payload }) => this.alertService.displayMessage(payload?.error?.description || payload.errorText, 'error'))
        ), { dispatch: false });

    deleteCategoryPrompts$ = createEffect(() =>
        this.actions$.pipe(
            ofType(deleteCategoryFromRestaurantPromptAction),
            switchMap(action =>
                this.alertService.displayQuestionWithLoader(
                    `Are you sure you want to delete category ${action.payload.name}?`)
                    .pipe(
                        filter(x => !!x),
                        map(x => action)
                    )),
            map(action => deleteCategoryFromRestaurantAction({ payload: action.payload }))
        )
    );

    deleteCategory$ = createEffect(() =>
        this.actions$.pipe(
            ofType(deleteCategoryFromRestaurantAction),
            withLatestFrom(this.store.select(loggedInUserIdSelector)),
            switchMap(([action, restaurantId]) =>
                this.restaurantService.deleteCategoryFromRestaurant(restaurantId, action.payload.id)
                    .pipe(
                        map(result => deleteCategoryFromRestaurantSuccessAction({ payload: action.payload })),
                        catchError(error => of(deleteCategoryFromRestaurantErrorAction({ payload: action.payload })))
                    ))
        ));

    deleteCategorySuccess$ = createEffect(() =>
        this.actions$.pipe(
            ofType(deleteCategoryFromRestaurantSuccessAction),
            tap(action => this.alertService.displayMessage(`Successfully deleted category ${action.payload.name}`, 'success'))
        ), { dispatch: false });

    deleteCategoryError$ = createEffect(() =>
        this.actions$.pipe(
            ofType(deleteCategoryFromRestaurantErrorAction),
            tap(action => this.alertService.displayMessage(`An error occurred while deleting category ${action.payload.name}. Try again later.`, 'error'))
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

    editRestaurantFood$ = createEffect(() =>
        this.actions$.pipe(
            ofType(editRestaurantFoodAction),
            withLatestFrom(this.store.select(loggedInUserIdSelector)),
            switchMap(([action, restaurantId]) =>
                this.restaurantService.editFood(restaurantId, action.payload.id, action.payload)
                    .pipe(
                        map(result => editRestaurantFoodSuccessAction({ payload: result })),
                        catchError(error => of(editRestaurantFoodErrorAction({ payload: error })))
                    ))
        ))

    editRestaurantFoodSuccess$ = createEffect(() =>
        this.actions$.pipe(
            ofType(editRestaurantFoodSuccessAction),
            tap(action => this.alertService.displayMessage('Successfully editted food.', 'success'))
        ), { dispatch: false });

    editRestaurantFoodError$ = createEffect(() =>
        this.actions$.pipe(
            ofType(editRestaurantFoodErrorAction),
            tap(({ payload }) => this.alertService.displayMessage(payload?.error?.description || 'An error occurred while editting food. Try again later.', 'error'))
        ), { dispatch: false });

    editRestaurant$ = createEffect(() =>
        this.actions$.pipe(
            ofType(editRestaurantAction),
            withLatestFrom(this.store.select(loggedInUserIdSelector)),
            switchMap(([action, restaurantId]) =>
                this.restaurantService.editRestaurant(restaurantId, action.payload)
                    .pipe(
                        map(result => editRestaurantSuccessAction({ payload: result })),
                        catchError(error => of(editRestaurantErrorAction({ payload: error })))
                    ))
        ));

    editRestaurantSuccess$ = createEffect(() =>
        this.actions$.pipe(
            ofType(editRestaurantSuccessAction),
            tap(({ payload }) => this.alertService.displayMessage('Successfully editted restaurant.', 'success'))
        ), { dispatch: false });

    editRestaurantErrors$ = createEffect(() =>
        this.actions$.pipe(
            ofType(editRestaurantErrorAction),
            tap(({ payload }) => this.alertService.displayMessage(payload?.error?.description || 'An error occurred while editting restaurant. Try again later.', 'error'))
        ), { dispatch: false });
}