import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { RxStomp } from "@stomp/rx-stomp";
import { filter, first, map, retry, switchMap, takeUntil, tap } from "rxjs/operators";
import { Order } from "src/app/customers/models/order";
import { OrderStatus } from "src/app/customers/models/order-status";
import { activateAction, deactivateAction, orderUpdateAction, subscribeToOrderUpdatesAction, subscribeToRestaurantOrdersAction, unsubscribeFromOrderUpdatesAction, unsubscribeFromRestaurantOrdersAction } from "./notification.actions";

@Injectable()
export class NotificationEffects {
    constructor(
        private actions$: Actions,
        private messageService: RxStomp
    ) { }

    activates$ = createEffect(() => this.actions$.pipe(
        ofType(activateAction),
        filter(_ => !this.messageService.connected()),
        tap(_ => this.messageService.activate())
    ), { dispatch: false });

    deactivates$ = createEffect(() =>
        this.actions$.pipe(
            ofType(deactivateAction),
            filter(_ => this.messageService.connected()),
            tap(_ => this.messageService.deactivate())
        ), { dispatch: false });

    orderUpdates$ = createEffect(() =>
        this.actions$.pipe(
            ofType(subscribeToOrderUpdatesAction),
            switchMap(({ payload }) =>
                this.messageService.watch(`/notifications/customers/${payload.customerId}/orders/${payload.orderId}`)
                    .pipe(
                        takeUntil(this.actions$.pipe(ofType(unsubscribeFromOrderUpdatesAction), first())),
                        map(data => (JSON.parse(data.body) as OrderStatus)),
                        map(data => orderUpdateAction({ payload: data })),
                        retry()
                    ))
        ))

    restaurantOrders$ = createEffect(() =>
        this.actions$.pipe(
            ofType(subscribeToRestaurantOrdersAction),
            switchMap(({ payload }) =>
                this.messageService.watch(`/notifications/restaurants/${payload}/orders`)
                    .pipe(
                        takeUntil(this.actions$.pipe(ofType(unsubscribeFromRestaurantOrdersAction), first())),
                        map(data => (JSON.parse(data.body) as Order)),
                        map(data => orderUpdateAction({ payload: data })),
                        retry()
                    ))
        ))
}