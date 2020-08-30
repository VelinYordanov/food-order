import { Component, Input, OnInit } from '@angular/core';
import { Address } from '../models/address';

@Component({
  selector: 'app-address-list',
  templateUrl: './address-list.component.html',
  styleUrls: ['./address-list.component.scss']
})
export class AddressListComponent implements OnInit {
  @Input() addresses: Address[];

  constructor() { }

  ngOnInit(): void {
  }

  removeAddressFromList(address: Address) {
    const index = this.addresses.indexOf(address);
    if(index !== -1) {
      this.addresses.splice(index, 1);
    }
  }
}
