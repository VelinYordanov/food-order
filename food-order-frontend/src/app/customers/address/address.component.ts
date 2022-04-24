import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Observable } from 'rxjs';
import { EnumsService } from 'src/app/shared/services/enums.service';
import { EnumData } from 'src/app/shared/models/enum-data';
import { Address } from '../models/address';

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
    private enumService: EnumsService) { }

  ngOnInit(): void {
    this.cities$ = this.enumService.getCities();
    this.addressTypes$ = this.enumService.getAddressTypes();

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
