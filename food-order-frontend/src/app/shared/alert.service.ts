import { Inject, Injectable } from '@angular/core';
import { from, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { SwalToken } from './injection-tokens/swal-injection-token';

@Injectable({
  providedIn: 'root'
})
export class AlertService {
  constructor(@Inject(SwalToken) private swal) { }

  displayRequestQuestion(questionTitle: string, preConfirm: Function, successTitle: string, errorBackupTitle: string, successFunction?: Function, errorFunction?: Function) {
    this.swal.fire({
      title: questionTitle,
      icon: 'question',
      showCancelButton: true,
      allowOutsideClick: () => !this.swal.isLoading(),
      showLoaderOnConfirm: true,
      preConfirm: preConfirm
    }).then(result => {
      if (!result.isDismissed) {
        if (result?.value?.error) {
          errorFunction && errorFunction();
          this.swal.fire({
            title: result?.value?.error?.description || errorBackupTitle,
            icon: 'error'
          })
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
}
