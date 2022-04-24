import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { Address } from '../models/address';
import { EnumsService } from '../../shared/services/enums.service';
import { EnumData } from 'src/app/shared/models/enum-data';
import { CustomerService } from '../services/customer.service';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
import { catchError, first, switchMap } from 'rxjs/operators';
import { EMPTY, Observable, Subject } from 'rxjs';
import { AlertService } from 'src/app/shared/services/alert.service';

@Component({
  selector: 'app-address-item',
  templateUrl: './address-item.component.html',
  styleUrls: ['./address-item.component.scss']
})
export class AddressItemComponent implements OnInit, OnDestroy {
  @Input() address: Address;
  @Output() onDelete = new EventEmitter<string>();

  private readonly deleteClicks$ = new Subject<void>();

  addressTypes: EnumData[];
  cities: EnumData[];

  constructor(
    private alertService: AlertService,
    private authenticationService: AuthenticationService,
    private enumsService: EnumsService,
    private customerService: CustomerService) { }

  ngOnInit(): void {
    this.enumsService.getCities().subscribe(cities => this.cities = cities);
    this.enumsService.getAddressTypes().subscribe(addressTypes => this.addressTypes = addressTypes);

    this.deleteClicks$
      .pipe(
        switchMap(_ => this.alertService.displayRequestQuestion<Address>(
          `Are you sure you want to delete this address?`,
          this.deleteAddress(),
          `Successfully deleted address ${this.address.id}`,
          `Error in deleting address ${this.address.id}. Try again later.`)
          .pipe(
            catchError(error => EMPTY)
          ))
      )
      .subscribe(_ => this.onDelete.next(this.address.id))
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

  private deleteAddress(): Observable<Address> {
    return this.authenticationService.user$
      .pipe(
        first(x => !!x),
        switchMap(user =>
          this.customerService.deleteCustomerAddress(user.id, this.address.id))
      )
  }
}
