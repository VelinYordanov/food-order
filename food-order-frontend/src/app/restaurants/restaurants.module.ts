import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { RestaurantsRoutingModule } from './restaurants-routing.module';
import { AngularMaterialModule } from '../angular-material.module';
import { RestaurantProfileComponent } from './restaurant-profile/restaurant-profile.component';
import { RestaurantAddFoodDialogComponent } from './restaurant-add-food-dialog/restaurant-add-food-dialog.component';
import { RestaurantFoodComponent } from './restaurant-food/restaurant-food.component';
import { DiscountCodeItemComponent } from './discount-code-item/discount-code-item.component';
import { DiscountCodesListComponent } from './discount-codes-list/discount-codes-list.component';
import { EditDiscountCodeComponent } from './edit-discount-code/edit-discount-code.component';
import { GenerateDiscountCodeComponent } from './generate-discount-code/generate-discount-code.component';
import { GraphsComponent } from './graphs/graphs.component';
import { MonthlyGraphComponent } from './monthly-graph/monthly-graph.component';
import { RestaurantOrderComponent } from './restaurant-order/restaurant-order.component';
import { RestaurantOrdersComponent } from './restaurant-orders/restaurant-orders.component';
import { YearlyGraphComponent } from './yearly-graph/yearly-graph.component';
import { ReactiveFormsModule } from '@angular/forms';
import { ChartsModule } from 'ng2-charts';

@NgModule({
  declarations: [
    RestaurantProfileComponent,
    RestaurantFoodComponent,
    RestaurantAddFoodDialogComponent,
    RestaurantOrdersComponent,
    RestaurantOrderComponent,
    GenerateDiscountCodeComponent,
    DiscountCodesListComponent,
    DiscountCodeItemComponent,
    EditDiscountCodeComponent,
    YearlyGraphComponent,
    MonthlyGraphComponent,
    GraphsComponent,
  ],
  imports: [
    CommonModule,
    AngularMaterialModule,
    RestaurantsRoutingModule,
    ReactiveFormsModule,
    ChartsModule,
  ],
})
export class RestaurantsModule {}
