import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EMPTY, Subject } from 'rxjs';
import { catchError, filter, switchMap, withLatestFrom } from 'rxjs/operators';
import { DiscountCode } from 'src/app/customers/models/discount-code';
import { AlertService } from 'src/app/shared/services/alert.service';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
import { EditDiscountCodeComponent } from '../edit-discount-code/edit-discount-code.component';
import { DiscountCodeItem } from '../models/discount-code-item';
import { RestaurantService } from '../services/restaurant.service';

@Component({
  selector: 'app-discount-code-item',
  templateUrl: './discount-code-item.component.html',
  styleUrls: ['./discount-code-item.component.scss'],
})
export class DiscountCodeItemComponent implements OnInit, OnDestroy {
  @Input() discountCode: DiscountCodeItem;

  @Output() onDelete = new EventEmitter<DiscountCode>();

  deleteClicks$ = new Subject<DiscountCode>();

  constructor(
    private authenticationService: AuthenticationService,
    private restaurantService: RestaurantService,
    private alertService: AlertService,
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.deleteClicks$
      .pipe(
        withLatestFrom(this.authenticationService.user$),
        switchMap(([discountCode, restaurant]) =>
          this.restaurantService
            .deleteDiscountCode(discountCode.id, restaurant.id)
            .pipe(
              catchError((error) => {
                this.alertService.displayMessage(
                  error?.error?.description ||
                    'An error occured while deleting discount code. Try again later.',
                  'error'
                );
                return EMPTY;
              })
            )
        )
      )
      .subscribe((discountCode) => {
        this.alertService.displayMessage(
          `Successfully deleted discount code ${discountCode.code}`,
          'success'
        );
        this.onDelete.emit(discountCode);
      });
  }

  openEditDialog(): void {
    const dialogRef = this.dialog.open(EditDiscountCodeComponent, {
      data: this.discountCode,
      autoFocus: false,
    });

    dialogRef.afterClosed()
    .pipe(
      filter(x => !!x)
    ).subscribe((edittedDiscountCode) => {
      this.discountCode = edittedDiscountCode;
    });
  }

  delete() {
    this.alertService
      .displayQuestion(
        `Are you sure you want to delete discount code ${this.discountCode.code}?`
      )
      .then((result) => result && this.deleteClicks$.next(this.discountCode));
  }

  ngOnDestroy(): void {
    this.deleteClicks$.complete();
  }
}
