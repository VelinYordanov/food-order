import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './home/login/login.component';
import { RestaurantListComponent } from './restaurants/restaurant-list/restaurant-list.component';
import { RestaurantProfileComponent } from './restaurants/restaurant-profile/restaurant-profile.component';


const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'profile', component: RestaurantProfileComponent },
  { path: 'restaurants', component: RestaurantListComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
