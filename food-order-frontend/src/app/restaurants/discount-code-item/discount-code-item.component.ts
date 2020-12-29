import { Component, Input, OnInit } from '@angular/core';
import { DiscountCodeItem } from '../models/discount-code-item';

@Component({
  selector: 'app-discount-code-item',
  templateUrl: './discount-code-item.component.html',
  styleUrls: ['./discount-code-item.component.scss'],
})
export class DiscountCodeItemComponent implements OnInit {
  @Input() discountCode: DiscountCodeItem;

  constructor() {}

  ngOnInit(): void {}
}
