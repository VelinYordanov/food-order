import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Subject } from 'rxjs';
import {
  filter,
  withLatestFrom,
} from 'rxjs/operators';
import { loggedInUserIdSelector } from 'src/app/store/authentication/authentication.selectors';
import { createDiscountCodeAction } from 'src/app/store/restaurants/discount-codes/discount-codes.actions';

@Component({
  selector: 'app-generate-discount-code',
  templateUrl: './generate-discount-code.component.html',
  styleUrls: ['./generate-discount-code.component.scss'],
})
export class GenerateDiscountCodeComponent implements OnInit {
  discountCodeForm: FormGroup;

  validFromMinDate = this.getStartDate();
  validToMinDate = this.getStartDate();

  private formSubmits$ = new Subject<void>();

  constructor(
    private store: Store,
    private formBuilder: FormBuilder) { }

  ngOnInit(): void {
    this.discountCodeForm = this.formBuilder.group({
      code: [
        null,
        [
          Validators.required,
          Validators.minLength(5),
          Validators.maxLength(10),
          Validators.pattern(/^\w*$/),
        ],
      ],
      discountPercentage: [
        null,
        [
          Validators.required,
          Validators.min(1),
          Validators.max(100),
          Validators.pattern(/\d*$/),
        ],
      ],
      validFrom: [null, Validators.required],
      validTo: [null, Validators.required],
      isSingleUse: [true],
      isOncePerUser: [false],
    });

    this.discountCodeForm
      .get('validFrom')
      .valueChanges.subscribe((date) => (this.validToMinDate = date));

    this.formSubmits$
      .pipe(
        filter((_) => this.discountCodeForm.valid),
        withLatestFrom(this.store.select(loggedInUserIdSelector))
      )
      .subscribe(([_, restaurantId]) => {
        const payload = { restaurantId, discountCode: this.discountCodeForm.value }
        this.store.dispatch(createDiscountCodeAction({ payload }));
      });
  }

  submit() {
    this.formSubmits$.next();
  }

  generateCode(event) {
    event.preventDefault();

    var result = '';
    const resultLength = Math.floor(Math.random() * (10 - 5 + 1) + 5);

    const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    var charactersLength = characters.length;

    for (var i = 0; i < resultLength; i++) {
      result += characters.charAt(Math.floor(Math.random() * charactersLength));
    }

    this.discountCodeForm.get('code').setValue(result);
  }

  private getStartDate(): Date {
    const now = new Date();
    return new Date(now.getTime() - now.getTimezoneOffset() * 60000);
  }
}
