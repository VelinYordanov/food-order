import { AbstractControl, ValidatorFn } from '@angular/forms';

export function matchingPasswords(): ValidatorFn {
  return (control: AbstractControl): { [key: string]: any } | null => {
    if (control.get('password').value === control.get('repeatPassword').value) {
      return null;
    }

    return { matchingPasswords: true };
  };
}
