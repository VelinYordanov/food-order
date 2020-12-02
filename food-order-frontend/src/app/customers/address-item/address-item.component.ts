import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Address } from '../models/address';
import { EnumsService } from '../../shared/services/enums.service';
import { EnumData } from 'src/app/shared/models/enum-data';
import { CustomerService } from '../services/customer.service';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
import { catchError, first, switchMap } from 'rxjs/operators';
import { EMPTY, Observable } from 'rxjs';
import { AlertService } from 'src/app/shared/services/alert.service';

@Component({
  selector: 'app-address-item',
  templateUrl: './address-item.component.html',
  styleUrls: ['./address-item.component.scss']
})
export class AddressItemComponent implements OnInit {
  @Input() address: Address;
  @Output() onDelete = new EventEmitter<Address>();

  addressTypes: EnumData[];
  cities: EnumData[];

  constructor(
    private alertService:AlertService,
    private authenticationService:AuthenticationService,
    private enumsService: EnumsService,
    private customerService: CustomerService) { }

  ngOnInit(): void {
    this.enumsService.getCities().subscribe(cities => this.cities = cities);
    this.enumsService.getAddressTypes().subscribe(addressTypes => this.addressTypes = addressTypes);
  }

  getCityValue(id: number) {
    return this.cities.find(city => city.id === id)?.value;
  }

  getAddressTypeValue(id: number) {
    return this.addressTypes.find(addressType => addressType.id === id)?.value;
  }

  delete() {
    this.alertService.displayRequestQuestion(
      `Are you sure you want to delete address ${this.address.id}?`,
      () => this.deleteAddress(),
      `Successfully deleted address ${this.address.id}`,
      `Error in deleting address ${this.address.id}. Try again later.`,
      () => this.onDelete.next(this.address))
  }

  private deleteAddress(): Observable<Address> {
    return this.authenticationService.user$
    .pipe(
      first(x => !!x),
      switchMap(user => 
        this.customerService.deleteCustomerAddress(user.id, this.address.id)
        .pipe(
          catchError(error => {
            this.alertService.displayMessage(error?.error?.description || `An error occurred while deleting address ${this.address.id}. Try again later.`, 'error');
            return EMPTY;
          })
        ))
    )
  }
}
