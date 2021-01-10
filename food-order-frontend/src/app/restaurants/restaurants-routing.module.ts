import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { RestaurantProfileComponent } from './restaurant-profile/restaurant-profile.component';
import { RestaurantOrdersComponent } from './restaurant-orders/restaurant-orders.component';
import { GraphsComponent } from './graphs/graphs.component';
import { GenerateDiscountCodeComponent } from './generate-discount-code/generate-discount-code.component';
import { DiscountCodesListComponent } from './discount-codes-list/discount-codes-list.component';

const routes: Routes = [
  {
    path: '',
    component: RestaurantProfileComponent,
  },
  { path: 'orders', component: RestaurantOrdersComponent },
  { path: 'graphs', component: GraphsComponent },
  {
    path: 'discount-codes/add',
    component: GenerateDiscountCodeComponent,
  },
  {
    path: 'discount-codes',
    component: DiscountCodesListComponent,
  },
];
@NgModule({
  declarations: [],
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class RestaurantsRoutingModule {}
