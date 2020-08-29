import { Component, Input, OnInit } from '@angular/core';
import { Address } from '../models/address';
import { EnumsService } from '../../shared/enums.service';
import { EnumData } from 'src/app/shared/models/enum-data';

@Component({
  selector: 'app-address-item',
  templateUrl: './address-item.component.html',
  styleUrls: ['./address-item.component.scss']
})
export class AddressItemComponent implements OnInit {
  @Input() address: Address;

  addressTypes: EnumData[];
  cities: EnumData[];

  constructor(private enumsService: EnumsService) { }

  ngOnInit(): void {
    console.log('here');
    this.enumsService.getCities().subscribe(cities => this.cities = cities);
    this.enumsService.getAddressTypes().subscribe(addressTypes => this.addressTypes = addressTypes);
  }

  getCityValue(id: number) {
    return this.cities.find(city => city.id === id)?.value;
  }

  getAddressTypeValue(id: number) {
    return this.addressTypes.find(addressType => addressType.id === id)?.value;
  }
}
