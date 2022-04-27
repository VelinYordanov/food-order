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
import { RxStomp } from '@stomp/rx-stomp';
import * as SockJs from 'sockjs-client';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { authenticationReducer } from './shared/store/authentication/authentication.reducer';
import { AuthenticationEffects } from './shared/store/authentication/authentication.effects';
import { routerReducer, StoreRouterConnectingModule } from '@ngrx/router-store';

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
    StoreModule.forRoot({ user: authenticationReducer, router: routerReducer }),
    EffectsModule.forRoot([AuthenticationEffects]),
    StoreDevtoolsModule.instrument({ maxAge: 25 }),
    StoreRouterConnectingModule.forRoot(),
  ],
  providers: [
    {
      provide: SwalToken,
      useFactory: () => Swal,
    },
    {
      provide: RxStomp, useFactory: () => {
        const rxstomp = new RxStomp();
        rxstomp.configure({
          webSocketFactory: () => new SockJs('/api/ws'),
          reconnectDelay: 5000,
          heartbeatIncoming: 4000,
          heartbeatOutgoing: 4000,
          connectHeaders: {
            Authorization: `Bearer ${localStorage.getItem('jwt-user')}`,
          },
        })

        return rxstomp;
      }
    }
  ],
  bootstrap: [AppComponent],
})
export class AppModule { }
