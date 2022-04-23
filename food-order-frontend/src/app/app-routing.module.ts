import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './home/login/login.component';
import { RestaurantDetailsComponent } from './restaurants/restaurant-details/restaurant-details.component';
import { RestaurantListComponent } from './restaurants/restaurant-list/restaurant-list.component';
import { RestaurantOnlyGuard } from './restaurants/guards/restaurant-only.guard';
import { CustomerOnlyGuard } from './customers/guards/customer-only.guard';
import { RegisterComponent } from './home/register/register.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: 'restaurant',
    canLoad: [RestaurantOnlyGuard],
    loadChildren: () =>
      import('./restaurants/restaurants.module').then(
        (m) => m.RestaurantsModule
      ),
  },
  {
    path: 'customer',
    canLoad: [CustomerOnlyGuard],
    loadChildren: () =>
      import('./customers/customers.module').then((m) => m.CustomersModule),
  },
  { path: 'restaurants', component: RestaurantListComponent },
  { path: 'restaurants/:id', component: RestaurantDetailsComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { relativeLinkResolution: 'legacy' })],
  exports: [RouterModule],
})
export class AppRoutingModule {}
