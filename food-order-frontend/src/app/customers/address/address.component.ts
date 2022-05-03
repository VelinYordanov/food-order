import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Observable } from 'rxjs';
import { EnumData } from 'src/app/shared/models/enum-data';
import { Address } from '../models/address';
import { Store } from '@ngrx/store';
import { loadCitiesAction, loadAddressTypesAction } from 'src/app/store/customers/enums/enums.actions';
import { citiesSelector, addressTypesSelector } from 'src/app/store/customers/enums/enums.selectors';

@Component({
  selector: 'app-address',
  templateUrl: './address.component.html',
  styleUrls: ['./address.component.scss']
})
export class AddressComponent implements OnInit {
  @Input() address: Address;
  @Output() onSubmit = new EventEmitter<Address>();

  addressForm: FormGroup;
  addressTypes$: Observable<EnumData[]>;
  cities$: Observable<EnumData[]>;

  constructor(
    private formBuilder: FormBuilder,
    private store: Store) { }

  ngOnInit(): void {
    this.store.dispatch(loadCitiesAction());
    this.store.dispatch(loadAddressTypesAction());

    this.cities$ = this.store.select(citiesSelector);
    this.addressTypes$ = this.store.select(addressTypesSelector);

    this.addressForm = this.formBuilder.group({
      city: [0],
      addressType: [0],
      neighborhood: [null],
      street: [null],
      streetNumber: [null],
      apartmentBuildingNumber: [null],
      entrance: [null],
      floor: [null],
      apartmentNumber: [null],
    })

    if (this.address) {
      this.addressForm.patchValue(this.address);
    }
  }

  submit() {
    if (this.addressForm.valid) {
      this.onSubmit.next(this.addressForm.value);
    }
  }
}
