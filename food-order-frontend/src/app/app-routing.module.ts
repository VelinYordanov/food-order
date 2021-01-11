import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AddressSelectComponent } from './home/address-select/address-select.component';
import { CheckoutComponent } from './home/checkout/checkout.component';
import { LoginComponent } from './home/login/login.component';
import { SuccessfulOrderComponent } from './home/successful-order/successful-order.component';
import { RestaurantDetailsComponent } from './restaurants/restaurant-details/restaurant-details.component';
import { RestaurantListComponent } from './restaurants/restaurant-list/restaurant-list.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: 'restaurant',
    loadChildren: () =>
      import('./restaurants/restaurants.module').then(
        (m) => m.RestaurantsModule
      ),
  },
  { path: 'order/checkout', component: CheckoutComponent },
  { path: 'order/address', component: AddressSelectComponent },
  { path: 'order/:id', component: SuccessfulOrderComponent },
  {
    path: 'customer',
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
