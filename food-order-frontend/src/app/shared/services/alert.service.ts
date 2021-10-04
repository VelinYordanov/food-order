import { Inject, Injectable } from '@angular/core';
import { from, Observable, throwError } from 'rxjs';
import { catchError, filter, map, tap } from 'rxjs/operators';
import { SwalToken } from '../injection-tokens/swal-injection-token';
import { AlertResult } from 'src/app/shared/models/alert-result';

@Injectable({
  providedIn: 'root'
})
export class AlertService {
  constructor(@Inject(SwalToken) private swal) { }

  displayRequestQuestion<T>(questionTitle: string, action: Observable<any>, successTitle: string, errorBackupTitle: string): Observable<T> {
    return from(this.swal.fire({
      title: questionTitle,
      icon: 'question',
      showCancelButton: true,
      allowOutsideClick: () => !this.swal.isLoading(),
      showLoaderOnConfirm: true,
      preConfirm: () => action
    })).pipe(
      catchError(error => {
        this.swal.fire({
          title: error?.error?.description || errorBackupTitle,
          icon: 'error'
        });

        return throwError(error);
      }),
      map(x => x as AlertResult<T>),
      tap(result => {
        if (result.isConfirmed) {
          this.swal.fire({
            title: successTitle,
            icon: "success"
          })
        }
      }),
      filter(result => result.isConfirmed),
      map(result => result.value)
    );
  }

  displayQuestion(question: string): Observable<boolean> {
    return from(
      this.swal.fire({
        title: question,
        showCancelButton: true,
        icon: 'question',
      })
    ).pipe(
      map(x => (x as any).isConfirmed)
    );
  }

  displayMessage(message: string, type: 'success' | 'error' | 'info') {
    this.swal.fire({
      title: message || 'An error occurred. Try again later.',
      icon: type
    })
  }

  private handleError(result: string, errorBackupTitle: string, errorFunction: Function) {
    errorFunction && errorFunction();
    this.swal.fire({
      title: result || errorBackupTitle,
      icon: 'error'
    })
  }
}
