import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ValidationService {
  constructor(private httpClient: HttpClient) {}

  checkIfEmailIsDisposable(email: string) {
    return this.httpClient.get<{ disposable: string }>(
      `https://disposable.debounce.io/?email=${email}`
    );
  }
}
