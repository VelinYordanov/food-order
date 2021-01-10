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
import { DiscountCodesListComponent } from './restaurants/discount-codes-list/discount-codes-list.component';
import { GraphsComponent } from './restaurants/graphs/graphs.component';
import { RestaurantOnlyGuard } from './restaurants/guards/restaurant-only.guard';
import { CustomerOnlyGuard } from './customers/guards/customer-only.guard';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: 'restaurant-profile',
    component: RestaurantProfileComponent,
    canActivate: [RestaurantOnlyGuard],
  },
  { path: 'restaurant-profile/orders', component: RestaurantOrdersComponent },
  { path: 'restaurant-profile/graphs', component: GraphsComponent },
  {
    path: 'restaurant-profile/discount-codes/add',
    component: GenerateDiscountCodeComponent,
  },
  {
    path: 'restaurant-profile/discount-codes',
    component: DiscountCodesListComponent,
  },
  { path: 'order/checkout', component: CheckoutComponent },
  { path: 'order/address', component: AddressSelectComponent },
  { path: 'order/:id', component: SuccessfulOrderComponent },
  {
    path: 'customer-profile',
    loadChildren: () =>
      import('./customers/customers.module').then((m) => m.CustomersModule),
  },
  { path: 'restaurants', component: RestaurantListComponent },
  { path: 'restaurants/:id', component: RestaurantDetailsComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
