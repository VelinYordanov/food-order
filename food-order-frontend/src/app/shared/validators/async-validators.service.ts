import { Injectable } from '@angular/core';
import { AbstractControl, AsyncValidatorFn } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { ValidationService } from '../../home/services/validation.service';

@Injectable({
  providedIn: 'root'
})
export class AsyncValidatorsService {
  constructor(private validationService: ValidationService) { }

  public checkIfDisposable() : AsyncValidatorFn{
    return (control: AbstractControl): Observable<{ [key: string]: any } | null> => {
      return this.validationService.checkIfEmailIsDisposable(control.value)
      .pipe(
        map(result => result.disposable === 'true' ? {disposable: true} : null),
        // Don't want to stop registration if API is offline
        catchError(error => of(null))
      )
    }
  }
}

