import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { EMPTY, Subject } from 'rxjs';
import { catchError, switchMap, switchMapTo } from 'rxjs/operators';
import { AlertService } from 'src/app/shared/services/alert.service';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
import { DiscountCodeItem } from '../models/discount-code-item';
import { RestaurantService } from '../services/restaurant.service';

@Component({
  selector: 'app-edit-discount-code',
  templateUrl: './edit-discount-code.component.html',
  styleUrls: ['./edit-discount-code.component.scss'],
})
export class EditDiscountCodeComponent implements OnInit, OnDestroy {
  discountCodeForm: FormGroup;

  validFromMinDate = this.getStartDate();
  validToMinDate = this.getStartDate();

  private formSubmits$ = new Subject<void>();

  constructor(
    private formBuilder: FormBuilder,
    private restaurantService: RestaurantService,
    private authenticationService: AuthenticationService,
    private alertService: AlertService,
    private dialogRef: MatDialogRef<EditDiscountCodeComponent>,
    @Inject(MAT_DIALOG_DATA) public discountCode: DiscountCodeItem
  ) {}

  ngOnInit(): void {
    this.discountCodeForm = this.formBuilder.group({
      discountPercentage: [
        {
          value: this.discountCode.discountPercentage,
          disabled: !!this.discountCode.timesUsed,
        },
        [
          Validators.required,
          Validators.min(1),
          Validators.max(100),
          Validators.pattern(/\d*$/),
        ],
      ],
      validFrom: [
        {
          value: this.discountCode.validFrom,
          disabled: !!this.discountCode.timesUsed,
        },
        Validators.required,
      ],
      validTo: [this.discountCode.validTo, Validators.required],
      isSingleUse: [this.discountCode.isSingleUse],
      isOncePerUser: [this.discountCode.isOncePerUser],
    });

    this.discountCodeForm
      .get('validFrom')
      .valueChanges.subscribe((date) => (this.validToMinDate = date));

    this.formSubmits$
      .pipe(
        switchMapTo(this.authenticationService.user$),
        switchMap((restaurant) =>
          this.restaurantService
            .editDiscountCode(
              this.discountCode.id,
              restaurant.id,
              this.discountCodeForm.value
            )
            .pipe(
              catchError((error) => {
                this.alertService.displayMessage(
                  error?.error?.description ||
                    'An error occurred while editting discount code. Try again later.',
                  'error'
                );
                return EMPTY;
              })
            )
        )
      )
      .subscribe((discountCode) => {
        this.alertService.displayMessage(
          `Successfully editted discount code ${discountCode.code}`,
          'success'
        );
        this.dialogRef.close(discountCode);
      });
  }

  ngOnDestroy(): void {
    this.formSubmits$.complete();
  }

  submit() {
    if (this.discountCodeForm.valid) {
      this.formSubmits$.next();
    }
  }

  close(event) {
    event.preventDefault();
    this.dialogRef.close();
  }

  private getStartDate(): Date {
    const now = new Date();
    return new Date(now.getTime() - now.getTimezoneOffset() * 60000);
  }
}
