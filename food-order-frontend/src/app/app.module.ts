import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { JwtModule } from '@auth0/angular-jwt';

import Swal from 'sweetalert2';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoginComponent } from './home/login/login.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { SwalToken } from 'src/app/shared/injection-tokens/swal-injection-token';
import { RestaurantListComponent } from './restaurants/restaurant-list/restaurant-list.component';
import { RestaurantItemComponent } from './restaurants/restaurant-item/restaurant-item.component';
import { RestaurantDetailsComponent } from './restaurants/restaurant-details/restaurant-details.component';
import { NavigationComponent } from './shared/navigation/navigation.component';

import { AngularMaterialModule } from './angular-material.module';
import { SharedModule } from './shared.module';
import { RegisterComponent } from './home/register/register.component';
import { RestaurantRegisterComponent } from './home/restaurant-register/restaurant-register.component';
import { RegisterCustomerComponent } from './home/register-customer/register-customer.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RestaurantListComponent,
    RestaurantItemComponent,
    RestaurantDetailsComponent,
    NavigationComponent,
    RegisterComponent,
    RestaurantRegisterComponent,
    RegisterCustomerComponent,
  ],
  imports: [
    JwtModule.forRoot({
      config: {
        tokenGetter: () => localStorage.getItem('jwt-user'),
        skipWhenExpired: true,
      },
    }),
    AngularMaterialModule,
    SharedModule,
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    FormsModule,
  ],
  providers: [
    {
      provide: SwalToken,
      useFactory: () => Swal,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
