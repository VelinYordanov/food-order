import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Actions, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { Subject } from 'rxjs';
import { first, switchMap, takeUntil } from 'rxjs/operators';
import { loggedInUserIdSelector } from 'src/app/store/authentication/authentication.selectors';
import { editDiscountCodeAction, editDiscountCodeSuccessAction } from 'src/app/store/restaurants/discount-codes/discount-codes.actions';
import { DiscountCodeItem } from '../models/discount-code-item';

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
  private onDestroy$ = new Subject<void>();

  constructor(
    private formBuilder: FormBuilder,
    private store: Store,
    private actions$: Actions,
    private dialogRef: MatDialogRef<EditDiscountCodeComponent>,
    @Inject(MAT_DIALOG_DATA) public discountCode: DiscountCodeItem
  ) { }

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
        switchMap(_ => this.store.select(loggedInUserIdSelector).pipe(first()))
      )
      .subscribe((restaurantId) => {
        const payload = {
          restaurantId,
          discountCode: this.discountCodeForm.getRawValue(),
          discountCodeId: this.discountCode.id
        }

        this.store.dispatch(editDiscountCodeAction({ payload }));
      });

    this.actions$.pipe(
      takeUntil(this.onDestroy$),
      ofType(editDiscountCodeSuccessAction)
    ).subscribe(action => this.dialogRef.close(action.payload));
  }

  ngOnDestroy(): void {
    this.onDestroy$.next();
    this.onDestroy$.complete();
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
