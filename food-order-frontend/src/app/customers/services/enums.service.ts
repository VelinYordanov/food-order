import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { EnumData } from '../models/enum-data';

@Injectable({
  providedIn: 'root'
})
export class EnumsService {
  private readonly BASE_URL: string = "/api/enums";

  constructor(private httpClient: HttpClient) { }

  getCities() {
    return this.httpClient.get<EnumData[]>(`${this.BASE_URL}/cities`)
  }

  getAddressTypes() {
    return this.httpClient.get<EnumData[]>(`${this.BASE_URL}/address-types`)
  }

  getOrderStatuses() {
    return this.httpClient.get<EnumData[]>(`${this.BASE_URL}/order-statuses`)
  }
}
