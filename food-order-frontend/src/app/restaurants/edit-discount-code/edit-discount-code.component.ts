import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { DiscountCodeItem } from '../models/discount-code-item';

@Component({
  selector: 'app-edit-discount-code',
  templateUrl: './edit-discount-code.component.html',
  styleUrls: ['./edit-discount-code.component.scss'],
})
export class EditDiscountCodeComponent implements OnInit, OnDestroy {
  discountCodeForm: FormGroup;

  validFromMinDate = new Date();
  validToMinDate = new Date();

  private formSubmits$ = new Subject<void>();

  constructor(
    private formBuilder: FormBuilder,
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
  }

  ngOnDestroy(): void {
    this.formSubmits$.complete();
  }

  submit() {
    this.formSubmits$.next();
  }

  close(event) {
    event.preventDefault();
    this.dialogRef.close();
  }
}
