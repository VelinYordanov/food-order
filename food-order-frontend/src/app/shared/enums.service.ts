import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { shareReplay } from 'rxjs/operators';
import { EnumData } from './models/enum-data';

@Injectable({
  providedIn: 'root'
})
export class EnumsService {
  private readonly BASE_URL: string = "/api/enums";

  private cities$: Observable<EnumData[]>;
  private addressTypes$: Observable<EnumData[]>;
  private orderStatuses$: Observable<EnumData[]>;

  constructor(private httpClient: HttpClient) { }

  getCities() {
    if (!this.cities$) {
      this.cities$ = this.httpClient.get<EnumData[]>(`${this.BASE_URL}/cities`)
        .pipe(
          shareReplay()
        );
    }

    return this.cities$;
  }

  getAddressTypes() {
    if (!this.addressTypes$) {
      this.addressTypes$ = this.httpClient.get<EnumData[]>(`${this.BASE_URL}/address-types`)
        .pipe(
          shareReplay()
        );
    }

    return this.addressTypes$;
  }

  getOrderStatuses() {
    if (!this.orderStatuses$) {
      this.orderStatuses$ = this.httpClient.get<EnumData[]>(`${this.BASE_URL}/order-statuses`)
        .pipe(
          shareReplay()
        );
    }

    return this.orderStatuses$;
  }
}
