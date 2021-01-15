import { FormControl, FormGroupDirective, NgForm } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';

export class RegisterErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(
    control: FormControl,
    form: FormGroupDirective | NgForm
  ): boolean {
    const invalidCtrl = !!(control && control.invalid && control.touched);
    const invalidParent = !!(
      form.hasError('matchingPasswords') && control.touched
    );

    return invalidCtrl || invalidParent;
  }
}
