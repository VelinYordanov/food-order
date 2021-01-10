import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CustomersRoutingModule } from './customers-routing.module';
import { CustomerProfileComponent } from './customer-profile/customer-profile.component';
import { AddressCreateComponent } from './address-create/address-create.component';
import { AddressUpdateComponent } from './address-update/address-update.component';
import { AddressListComponent } from './address-list/address-list.component';
import { ReactiveFormsModule } from '@angular/forms';
import { CustomerOrderComponent } from './customer-order/customer-order.component';
import { CustomerOrdersComponent } from './customer-orders/customer-orders.component';
import { AddressComponent } from './address/address.component';
import { AddressItemComponent } from './address-item/address-item.component';

import { AngularMaterialModule } from '../angular-material.module';

@NgModule({
  declarations: [
    CustomerProfileComponent,
    AddressCreateComponent,
    AddressUpdateComponent,
    AddressListComponent,
    CustomerOrderComponent,
    CustomerOrdersComponent,
    AddressListComponent,
    AddressComponent,
    AddressItemComponent,
  ],
  imports: [
    CommonModule,
    CustomersRoutingModule,
    ReactiveFormsModule,
    AngularMaterialModule,
  ],
})
export class CustomersModule {}
