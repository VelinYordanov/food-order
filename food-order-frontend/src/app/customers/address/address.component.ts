import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { combineLatest, Observable, throwError } from 'rxjs';
import { catchError, first, map, switchMap } from 'rxjs/operators';
import { AlertService } from 'src/app/shared/alert.service';
import { AuthenticationService } from 'src/app/shared/authentication.service';
import { EnumsService } from 'src/app/shared/enums.service';
import { EnumData } from 'src/app/shared/models/enum-data';
import { Address } from '../models/address';
import { CustomerService } from '../services/customer.service';

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
    private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
    private authenticationService: AuthenticationService,
    private customerService: CustomerService,
    private alertService: AlertService,
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

    // combineLatest([
    //   this.authenticationService.user$.pipe(first(user => !!user)),
    //   this.activatedRoute.paramMap
    //     .pipe(
    //       map(paramMap => paramMap.get('id')),
    //       first(id => !!id)
    //     )
    // ])
    //   .pipe(
    //     switchMap(([user, id]) =>
    //       this.customerService.getCustomerAddress(user.id, id)
    //         .pipe(
    //           catchError(error => {
    //             this.alertService.displayMessage('An error occurred while loading address. Try again later.', 'error');
    //             return throwError(error);
    //           })
    //         ))
    //   ).subscribe(address => this.addressForm.patchValue(address))
  }

  submit() {
    console.log(this.addressForm.value);
    if (this.addressForm.valid) {
      this.onSubmit.next(this.addressForm.value);
    }
  }
}
