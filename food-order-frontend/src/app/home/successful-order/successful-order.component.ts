import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { combineLatest, EMPTY, Observable, Subject } from 'rxjs';
import { catchError, filter, map, switchMap, switchMapTo, takeUntil, tap, withLatestFrom } from 'rxjs/operators';
import { Order } from 'src/app/customers/models/order';
import { CustomerService } from 'src/app/customers/services/customer.service';
import { AlertService } from 'src/app/shared/services/alert.service';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
import { EnumsService } from 'src/app/shared/services/enums.service';
import { EnumData } from 'src/app/shared/models/enum-data';
import { RealTimeNotificationsService } from 'src/app/shared/services/real-time-notifications.service';
import { OrderStatus } from '../../customers/models/order-status';

@Component({
  selector: 'app-successful-order',
  templateUrl: './successful-order.component.html',
  styleUrls: ['./successful-order.component.scss']
})
export class SuccessfulOrderComponent implements OnInit, OnDestroy {
  order: Order;

  private onDestroy$: Subject<void> = new Subject<void>();
  private orderStatuses: EnumData[] = [];

  constructor(
    private activatedRoute: ActivatedRoute,
    private alertService: AlertService,
    private enumService: EnumsService,
    private authenticationService: AuthenticationService,
    private customerService: CustomerService,
    private realTimeNotificationsService: RealTimeNotificationsService) { }

  ngOnInit(): void {
    this.enumService.getOrderStatuses().subscribe(
      orderStatuses => this.orderStatuses = orderStatuses,
      error => this.alertService.displayMessage(error?.error?.description || 'An error occurred while loading order statuses. Try again later.', 'error'));

    const orderId$ = this.activatedRoute.paramMap
      .pipe(
        map(paramMap => paramMap.get('id')),
        filter(id => !!id)
      );

     const userId$ = this.authenticationService.user$
        .pipe(
          filter(user => !!user),
          map(user => user.id)
        );

    combineLatest(
      [
        orderId$,
        userId$
      ]
    ).pipe(
      takeUntil(this.onDestroy$),
      switchMap(([orderId, userId]) =>
        this.customerService.getOrderById(userId, orderId)
          .pipe(
            catchError(error => {
              this.alertService.displayMessage(error?.error?.description || 'An error occurred while loading offer. Try again later.', 'error');
              return EMPTY;
            })
          ))
    ).subscribe(order => this.order = order);

    userId$
      .pipe(
        takeUntil(this.onDestroy$),
        withLatestFrom(orderId$),
        switchMap((([userId, orderId]) => this.realTimeNotificationsService
          .subscribe(`/notifications/customers/${userId}/orders/${orderId}`)
          .pipe(
            takeUntil(this.onDestroy$)
          )))
      ).subscribe(order => {
        if(this.order) {
          const orderStatus = JSON.parse(order) as OrderStatus;
          this.order.status = orderStatus.status;
        }
      });
  }

  ngOnDestroy(): void {
    this.onDestroy$.next();
    this.onDestroy$.complete();
  }

  getOrderStatus(status: number): string {
    return this.orderStatuses?.find(orderStatus => orderStatus.id === status)?.value;
  }
}
