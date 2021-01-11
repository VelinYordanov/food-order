import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { AngularMaterialModule } from './angular-material.module';
import { CartItemsComponent } from './customers/cart-items/cart-items.component';
import { CartComponent } from './customers/cart/cart-component';

@NgModule({
  declarations: [CartComponent, CartItemsComponent],
  imports: [CommonModule, AngularMaterialModule],
  exports: [CartComponent, CartItemsComponent],
})
export class SharedModule {}
