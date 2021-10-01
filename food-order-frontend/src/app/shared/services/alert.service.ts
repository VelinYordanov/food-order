import { Inject, Injectable } from '@angular/core';
import { EMPTY, Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { SwalToken } from '../injection-tokens/swal-injection-token';

@Injectable({
  providedIn: 'root'
})
export class AlertService {
  constructor(@Inject(SwalToken) private swal) { }

  displayRequestQuestion(questionTitle: string, action: Observable<any>, successTitle: string, errorBackupTitle: string, successFunction?: Function, errorFunction?: Function) {
    this.swal.fire({
      title: questionTitle,
      icon: 'question',
      showCancelButton: true,
      allowOutsideClick: () => !this.swal.isLoading(),
      showLoaderOnConfirm: true,
      preConfirm: () => action
        .pipe(
          catchError(error => {
            this.handleError(error?.error?.description, errorBackupTitle, errorFunction);
            return EMPTY;
          })
        )
    }).then(result => {
      if (!result.isDismissed) {
        if (result?.value?.error) {
          this.handleError(result?.value?.error?.description, errorBackupTitle, errorFunction);
        } else {
          successFunction && successFunction();
          this.swal.fire({
            title: successTitle,
            icon: "success"
          })
        }
      }
    })
  }

  displayQuestion(question: string): Promise<boolean> {
    return this.swal.fire({
      title: question,
      showCancelButton: true,
      icon: 'question',
    }).then(answer => answer.isConfirmed)
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
