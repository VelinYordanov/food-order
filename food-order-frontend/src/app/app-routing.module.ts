import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './home/login/login.component';
import { RestaurantProfileComponent } from './restaurants/restaurant-profile/restaurant-profile.component';


const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'profile', component: RestaurantProfileComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
