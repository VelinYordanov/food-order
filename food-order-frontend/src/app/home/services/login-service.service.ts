import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { LoginUser } from '../models/login-user';
import { JwtToken } from '../models/jwt-token';

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  constructor(private httpClient: HttpClient) { }

  loginRestaurant(user: LoginUser) {
    return this.httpClient.post<JwtToken>('/api/restaurants/tokens', user);
  }

  loginCustomer(user: LoginUser) {
    return this.httpClient.post<JwtToken>('/api/customers/tokens', user);
  }
}
