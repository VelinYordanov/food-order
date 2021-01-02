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
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatMomentDateModule, MAT_MOMENT_DATE_ADAPTER_OPTIONS } from '@angular/material-moment-adapter';

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
import { CustomerProfileComponent } from './customers/customer-profile/customer-profile.component';
import { AddressItemComponent } from './customers/address-item/address-item.component';
import { AddressListComponent } from './customers/address-list/address-list.component';
import { AddressComponent } from './customers/address/address.component';
import { AddressCreateComponent } from './customers/address-create/address-create.component';
import { AddressUpdateComponent } from './customers/address-update/address-update.component';
import { CartItemsComponent } from './home/cart-items/cart-items.component';
import { CheckoutComponent } from './home/checkout/checkout.component';
import { AddressSelectComponent } from './home/address-select/address-select.component';
import { SuccessfulOrderComponent } from './home/successful-order/successful-order.component';
import { CutomerOrdersComponent } from './customers/customer-orders/customer-orders.component';
import { RestaurantOrdersComponent } from './restaurants/restaurant-orders/restaurant-orders.component';
import { RestaurantOrderComponent } from './restaurants/restaurant-order/restaurant-order.component';
import { CustomerOrderComponent } from './customers/customer-order/customer-order.component';
import { GenerateDiscountCodeComponent } from './restaurants/generate-discount-code/generate-discount-code.component';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { DiscountCodesListComponent } from './restaurants/discount-codes-list/discount-codes-list.component';
import { DiscountCodeItemComponent } from './restaurants/discount-code-item/discount-code-item.component';
import { EditDiscountCodeComponent } from './restaurants/edit-discount-code/edit-discount-code.component';
import { MatTooltipModule } from '@angular/material/tooltip';
import { YearlyGraphComponent } from './restaurants/yearly-graph/yearly-graph.component';
import { MonthlyGraphComponent } from './restaurants/monthly-graph/monthly-graph.component';
import { GraphsComponent } from './restaurants/graphs/graphs.component';

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
    AddressUpdateComponent,
    CartItemsComponent,
    CheckoutComponent,
    AddressSelectComponent,
    SuccessfulOrderComponent,
    CutomerOrdersComponent,
    RestaurantOrdersComponent,
    RestaurantOrderComponent,
    CustomerOrderComponent,
    GenerateDiscountCodeComponent,
    DiscountCodesListComponent,
    DiscountCodeItemComponent,
    EditDiscountCodeComponent,
    YearlyGraphComponent,
    MonthlyGraphComponent,
    GraphsComponent,
  ],
  imports: [
    JwtModule.forRoot({
      config: {
        tokenGetter: () => localStorage.getItem('jwt-user'),
        skipWhenExpired: true,
      },
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
    MatPaginatorModule,
    MatDatepickerModule,
    MatMomentDateModule,
    MatTooltipModule,

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
    { provide: MAT_DATE_LOCALE, useValue: 'en-GB' },
    { provide: MAT_MOMENT_DATE_ADAPTER_OPTIONS, useValue: { useUtc: true }}
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
