import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { combineLatest, EMPTY, Observable, Subject, throwError } from 'rxjs';
import { catchError, filter, first, map, retry, switchMap, switchMapTo, takeUntil, tap, withLatestFrom } from 'rxjs/operators';
import { Order } from 'src/app/customers/models/order';
import { CustomerService } from 'src/app/customers/services/customer.service';
import { AlertService } from 'src/app/shared/services/alert.service';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
import { EnumsService } from 'src/app/shared/services/enums.service';
import { EnumData } from 'src/app/shared/models/enum-data';
import { OrderStatus } from '../models/order-status';
import { RxStomp, RxStompState } from '@stomp/rx-stomp';

@Component({
  selector: 'app-successful-order',
  templateUrl: './successful-order.component.html',
  styleUrls: ['./successful-order.component.scss']
})
export class SuccessfulOrderComponent implements OnInit, OnDestroy {
  order: Order;

  private orderStatuses: EnumData[] = [];
  private cancel$ = new Subject<void>();

  constructor(
    private messageService: RxStomp,
    private activatedRoute: ActivatedRoute,
    private alertService: AlertService,
    private enumService: EnumsService,
    private authenticationService: AuthenticationService,
    private customerService: CustomerService) { }

  ngOnInit(): void {
    this.messageService.connectionState$
      .pipe(
        first(),
        filter(x => x !== RxStompState.OPEN)
      ).subscribe(_ => this.messageService.activate());
      
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
        withLatestFrom(orderId$),
        switchMap((([userId, orderId]) => this.messageService.watch(`/notifications/customers/${userId}/orders/${orderId}`)
          .pipe(
            map(data => data.body),
            retry(),
            catchError(error => EMPTY),
            takeUntil(this.cancel$)
          )))
      ).subscribe(order => {
        if (this.order) {
          const orderStatus = JSON.parse(order) as OrderStatus;
          this.order.status = orderStatus.status;
        }
      });
  }

  ngOnDestroy(): void {
    this.cancel$.next();
    this.messageService.deactivate();
  }

  getOrderStatus(status: number): string {
    return this.orderStatuses?.find(orderStatus => orderStatus.id === status)?.value;
  }
}
