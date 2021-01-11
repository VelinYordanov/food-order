import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AddressCreateComponent } from './address-create/address-create.component';
import { AddressSelectComponent } from './address-select/address-select.component';
import { AddressUpdateComponent } from './address-update/address-update.component';
import { CheckoutComponent } from './checkout/checkout.component';
import { CustomerProfileComponent } from './customer-profile/customer-profile.component';
import { SuccessfulOrderComponent } from './successful-order/successful-order.component';

const routes: Routes = [
  { path: 'profile', component: CustomerProfileComponent },
  { path: 'addresses/add', component: AddressCreateComponent },
  { path: 'addresses/:id', component: AddressUpdateComponent },
  { path: 'order/checkout', component: CheckoutComponent },
  { path: 'order/address', component: AddressSelectComponent },
  { path: 'order/:id', component: SuccessfulOrderComponent },
];

@NgModule({
  declarations: [],
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CustomersRoutingModule {}
