import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Address } from '../models/address';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {
  private readonly BASE_URL = '/api/customers';

  constructor(private httpClient: HttpClient) { }

  getCustomerAddresses(customerId:string) {
    return this.httpClient.get<Address[]>(`${this.BASE_URL}/${customerId}/addresses`);
  }
}
