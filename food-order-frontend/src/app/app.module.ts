import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { JwtModule } from '@auth0/angular-jwt';

import Swal from 'sweetalert2';

import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatDialogModule } from '@angular/material/dialog';
import { MatCardModule } from '@angular/material/card';
import { MatSelectModule } from '@angular/material/select';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoginComponent } from './home/login/login.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { RestaurantProfileComponent } from './restaurants/restaurant-profile/restaurant-profile.component';
import { SwalToken } from 'src/app/shared/injection-tokens/swal-injection-token';
import { RestaurantFoodComponent } from './restaurants/restaurant-food/restaurant-food.component';
import { RestaurantAddFoodDialogComponent } from './restaurants/restaurant-add-food-dialog/restaurant-add-food-dialog.component';
import { RestaurantListComponent } from './restaurants/restaurant-list/restaurant-list.component';
import { RestaurantItemComponent } from './restaurants/restaurant-item/restaurant-item.component';
import { RestaurantDetailsComponent } from './restaurants/restaurant-details/restaurant-details.component';
import { CartComponent } from './home/cart-component/cart-component';
import { CustomerProfileComponent } from './customers/customer-profile/customer-profile.component';
import { AddressItemComponent } from './customers/address-item/address-item.component';
import { AddressListComponent } from './customers/address-list/address-list.component';
import { AddressComponent } from './customers/address/address.component';
import { AddressCreateComponent } from './customers/address-create/address-create.component';
import { AddressUpdateComponent } from './customers/address-update/address-update.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RestaurantProfileComponent,
    RestaurantFoodComponent,
    RestaurantAddFoodDialogComponent,
    RestaurantListComponent,
    RestaurantItemComponent,
    RestaurantDetailsComponent,
    CartComponent,
    CustomerProfileComponent,
    AddressItemComponent,
    AddressListComponent,
    AddressComponent,
    AddressCreateComponent,
    AddressUpdateComponent
  ],
  imports: [
    JwtModule.forRoot({
      config: {
        tokenGetter: () => localStorage.getItem("jwt-user"),
        skipWhenExpired: true
      }
    }),

    MatFormFieldModule,
    MatInputModule,
    MatCheckboxModule,
    MatButtonModule,
    MatChipsModule,
    MatIconModule,
    MatAutocompleteModule,
    MatDialogModule,
    MatCardModule,
    MatSelectModule,
    
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    FormsModule
  ],
  providers: [{
    provide: SwalToken,
    useFactory: () => Swal
  }],
  bootstrap: [AppComponent]
})
export class AppModule { }
