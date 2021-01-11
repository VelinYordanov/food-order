import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AddressCreateComponent } from './address-create/address-create.component';
import { AddressUpdateComponent } from './address-update/address-update.component';
import { CustomerProfileComponent } from './customer-profile/customer-profile.component';

const routes: Routes = [
  { path: 'profile', component: CustomerProfileComponent },
  { path: 'addresses/add', component: AddressCreateComponent },
  { path: 'addresses/:id', component: AddressUpdateComponent },
];

@NgModule({
  declarations: [],
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CustomersRoutingModule {}
