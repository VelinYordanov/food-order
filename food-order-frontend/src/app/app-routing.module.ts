import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AddressCreateComponent } from './customers/address-create/address-create.component';
import { AddressUpdateComponent } from './customers/address-update/address-update.component';
import { CustomerProfileComponent } from './customers/customer-profile/customer-profile.component';
import { RestaurantOrdersComponent } from './restaurants/restaurant-orders/restaurant-orders.component';
import { AddressSelectComponent } from './home/address-select/address-select.component';
import { CheckoutComponent } from './home/checkout/checkout.component';
import { LoginComponent } from './home/login/login.component';
import { SuccessfulOrderComponent } from './home/successful-order/successful-order.component';
import { RestaurantDetailsComponent } from './restaurants/restaurant-details/restaurant-details.component';
import { RestaurantListComponent } from './restaurants/restaurant-list/restaurant-list.component';
import { RestaurantProfileComponent } from './restaurants/restaurant-profile/restaurant-profile.component';
import { GenerateDiscountCodeComponent } from './restaurants/generate-discount-code/generate-discount-code.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'restaurant-profile', component: RestaurantProfileComponent },
  { path: 'restaurant-profile/orders', component: RestaurantOrdersComponent },
  {
    path: 'restaurant-profile/discount-codes/add',
    component: GenerateDiscountCodeComponent,
  },
  { path: 'order/checkout', component: CheckoutComponent },
  { path: 'order/address', component: AddressSelectComponent },
  { path: 'order/:id', component: SuccessfulOrderComponent },
  { path: 'customer-profile', component: CustomerProfileComponent },
  { path: 'customer-profile/addresses/add', component: AddressCreateComponent },
  { path: 'customer-profile/addresses/:id', component: AddressUpdateComponent },
  { path: 'restaurants', component: RestaurantListComponent },
  { path: 'restaurants/:id', component: RestaurantDetailsComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
