import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Address } from '../models/address';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {
  private readonly BASE_URL = '/api/customers';

  constructor(private httpClient: HttpClient) { }

  getCustomerAddresses(customerId: string) {
    return this.httpClient.get<Address[]>(`${this.BASE_URL}/${customerId}/addresses`);
  }

  getCustomerAddress(customerId: string, addressId: string) {
    return this.httpClient.get<Address>(`${this.BASE_URL}/${customerId}/addresses/${addressId}`);
  }

  deleteCustomerAddress(customerId: string, addressId: string) {
    return this.httpClient.delete<Address>(`${this.BASE_URL}/${customerId}/addresses/${addressId}`);
  }

  addAddressToCustomer(customerId: string, address: Address) {
    return this.httpClient.post(`${this.BASE_URL}/${customerId}/addresses`, address);
  }

  editCustomerAddress(customerId:string, addressId:string, address:Address) {
    return this.httpClient.put(`${this.BASE_URL}/${customerId}/addresses/${addressId}`, address);
  }
}
