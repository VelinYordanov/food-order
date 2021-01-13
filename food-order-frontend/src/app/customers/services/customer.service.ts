import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Address } from '../models/address';
import { OrderCreate } from '../models/order-create';
import { Order } from '../models/order';
import { DiscountCode } from '../models/discount-code';
import { Page } from '../../shared/models/page';
import { CustomerRegisterDto } from '../../home/models/customer-register-dto';
import { Observable } from 'rxjs';
import { JwtToken } from 'src/app/home/models/jwt-token';

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

  getOrders(customerId, pageNumber = 0) {
    return this.httpClient.get<Page<Order>>(`${this.BASE_URL}/${customerId}/orders`, {params: { page : String(pageNumber)}});
  }

  submitOrder(order: OrderCreate) {
    return this.httpClient.post<Order>(`${this.BASE_URL}/${order.customerId}/orders`, order);
  }

  getOrderById(customerId: string, orderId: string) {
    return this.httpClient.get<Order>(`${this.BASE_URL}/${customerId}/orders/${orderId}`);
  }

  getDiscountCode(restaurantId: string, code: string) {
    return this.httpClient.get<DiscountCode>(`/api/restaurants/${restaurantId}/discount-codes/${code}`);
  }

  registerCustomer(customer: CustomerRegisterDto): Observable<JwtToken> {
    return this.httpClient.post<JwtToken>(`${this.BASE_URL}`, customer);
  }
}
