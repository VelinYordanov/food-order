import { Injectable } from '@angular/core';
import { Address } from 'src/app/customers/models/address';
import { OrderFoodResponse } from 'src/app/customers/models/order-food-response';

@Injectable({
  providedIn: 'root'
})
export class UtilService {
  constructor() { }

  getAddressData(address: Address) {
    return [address.neighborhood, address.street, address.streetNumber, address.apartmentBuildingNumber]
      .filter(Boolean)
      .join(", ");
  }

  calculateTotal(foods: OrderFoodResponse[]) {
    return foods.reduce((acc, curr) => acc + curr.price, 0);
  }

  calculateTotalWithDiscount(foods: OrderFoodResponse[], discountPercentage: number) {
    return this.calculateTotal(foods) * ((100 - discountPercentage) / 100);
  }
}
