import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { combineLatest, EMPTY, Observable } from 'rxjs';
import { catchError, filter, map, switchMap } from 'rxjs/operators';
import { Order } from 'src/app/customers/models/order';
import { CustomerService } from 'src/app/customers/services/customer.service';
import { AlertService } from 'src/app/shared/services/alert.service';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
import { EnumsService } from 'src/app/shared/services/enums.service';
import { EnumData } from 'src/app/shared/models/enum-data';

@Component({
  selector: 'app-successful-order',
  templateUrl: './successful-order.component.html',
  styleUrls: ['./successful-order.component.scss']
})
export class SuccessfulOrderComponent implements OnInit {
  order$: Observable<Order> = new Observable<Order>();

  private orderStatuses: EnumData[] = [];

  constructor(
    private activatedRoute: ActivatedRoute,
    private alertService: AlertService,
    private enumService: EnumsService,
    private authenticationService: AuthenticationService,
    private customerService: CustomerService) { }

  ngOnInit(): void {
    this.enumService.getOrderStatuses().subscribe(
      orderStatuses => this.orderStatuses = orderStatuses,
      error => this.alertService.displayMessage(error?.error?.description || 'An error occurred while loading order statuses. Try again later.', 'error'));

    this.order$ = combineLatest(
      [
        this.activatedRoute.paramMap
          .pipe(
            map(paramMap => paramMap.get('id')),
            filter(id => !!id)
          ),
        this.authenticationService.user$
          .pipe(
            filter(user => !!user),
            map(user => user.id)
          )
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
    )
  }

  getOrderStatus(status: number): string {
    return this.orderStatuses?.find(orderStatus => orderStatus.id === status)?.value;
  }
}
