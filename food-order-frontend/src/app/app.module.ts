import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { JwtModule } from '@auth0/angular-jwt';

import Swal from 'sweetalert2';

import { ChartsModule } from 'ng2-charts';

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
import { CartComponent } from './home/cart/cart-component';
import { CartItemsComponent } from './home/cart-items/cart-items.component';
import { CheckoutComponent } from './home/checkout/checkout.component';
import { AddressSelectComponent } from './home/address-select/address-select.component';
import { SuccessfulOrderComponent } from './home/successful-order/successful-order.component';
import { RestaurantOrdersComponent } from './restaurants/restaurant-orders/restaurant-orders.component';
import { RestaurantOrderComponent } from './restaurants/restaurant-order/restaurant-order.component';
import { GenerateDiscountCodeComponent } from './restaurants/generate-discount-code/generate-discount-code.component';
import { DiscountCodesListComponent } from './restaurants/discount-codes-list/discount-codes-list.component';
import { DiscountCodeItemComponent } from './restaurants/discount-code-item/discount-code-item.component';
import { EditDiscountCodeComponent } from './restaurants/edit-discount-code/edit-discount-code.component';
import { YearlyGraphComponent } from './restaurants/yearly-graph/yearly-graph.component';
import { MonthlyGraphComponent } from './restaurants/monthly-graph/monthly-graph.component';
import { GraphsComponent } from './restaurants/graphs/graphs.component';
import { NavigationComponent } from './shared/navigation/navigation.component';

import { AngularMaterialModule } from './angular-material.module';

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
    CartItemsComponent,
    CheckoutComponent,
    AddressSelectComponent,
    SuccessfulOrderComponent,
    RestaurantOrdersComponent,
    RestaurantOrderComponent,
    GenerateDiscountCodeComponent,
    DiscountCodesListComponent,
    DiscountCodeItemComponent,
    EditDiscountCodeComponent,
    YearlyGraphComponent,
    MonthlyGraphComponent,
    GraphsComponent,
    NavigationComponent,
  ],
  imports: [
    JwtModule.forRoot({
      config: {
        tokenGetter: () => localStorage.getItem('jwt-user'),
        skipWhenExpired: true,
      },
    }),
    AngularMaterialModule,

    ChartsModule,

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
