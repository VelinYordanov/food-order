import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Address } from '../models/address';
import { OrderCreate } from '../models/order-create';
import { Order } from '../models/order';
import { DiscountCode } from '../models/discount-code';

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

  editCustomerAddress(customerId: string, addressId: string, address: Address) {
    return this.httpClient.put(`${this.BASE_URL}/${customerId}/addresses/${addressId}`, address);
  }

  submitOrder(order: OrderCreate) {
    return this.httpClient.post<Order>(`${this.BASE_URL}/${order.customerId}/orders`, order);
  }

  getDiscountCode(restaurantId: string, code: string) {
    return this.httpClient.get<DiscountCode>(`/api/restaurants/${restaurantId}/discount-codes/${code}`);
  }

  getAddressData(address: Address) {
    return [address.neighborhood, address.street, address.streetNumber, address.apartmentBuildingNumber]
      .filter(Boolean)
      .join(", ");
  }
}
