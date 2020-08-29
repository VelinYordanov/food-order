import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CustomerProfileComponent } from './customers/customer-profile/customer-profile.component';
import { LoginComponent } from './home/login/login.component';
import { RestaurantDetailsComponent } from './restaurants/restaurant-details/restaurant-details.component';
import { RestaurantListComponent } from './restaurants/restaurant-list/restaurant-list.component';
import { RestaurantProfileComponent } from './restaurants/restaurant-profile/restaurant-profile.component';


const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'restaurant-profile', component: RestaurantProfileComponent },
  { path: 'customer-profile', component: CustomerProfileComponent },
  { path: 'restaurants', component: RestaurantListComponent },
  { path: 'restaurants/:id', component: RestaurantDetailsComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
