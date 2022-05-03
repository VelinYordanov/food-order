import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Address } from '../models/address';
import { Subject } from 'rxjs';
import { Store } from '@ngrx/store';
import { deleteAddressPromptAction } from '../../store/customers/addresses/addresses.actions';
import { EnumData } from 'src/app/shared/models/enum-data';
import { loadCitiesAction, loadAddressTypesAction } from 'src/app/store/customers/enums/enums.actions';
import { citiesSelector, addressTypesSelector } from 'src/app/store/customers/enums/enums.selectors';

@Component({
  selector: 'app-address-item',
  templateUrl: './address-item.component.html',
  styleUrls: ['./address-item.component.scss']
})
export class AddressItemComponent implements OnInit, OnDestroy {
  @Input() address: Address;

  addressTypes: EnumData[];
  cities: EnumData[];

  private readonly deleteClicks$ = new Subject<void>();

  constructor(
    private store: Store) { }

  ngOnInit(): void {
    this.store.dispatch(loadCitiesAction());
    this.store.dispatch(loadAddressTypesAction());

    this.store.select(citiesSelector).subscribe(cities => this.cities = cities);
    this.store.select(addressTypesSelector).subscribe(addressTypes => this.addressTypes = addressTypes);

    this.deleteClicks$.subscribe(_ => this.store.dispatch(deleteAddressPromptAction({ payload: this.address })))
  }

  ngOnDestroy(): void {
    this.deleteClicks$.complete();
  }

  getCityValue(id: number) {
    return this.cities.find(city => city.id === id)?.value;
  }

  getAddressTypeValue(id: number) {
    return this.addressTypes.find(addressType => addressType.id === id)?.value;
  }

  delete() {
    this.deleteClicks$.next();
  }
}
