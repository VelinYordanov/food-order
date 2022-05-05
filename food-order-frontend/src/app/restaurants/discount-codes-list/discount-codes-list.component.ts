import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { selectDiscountCodes } from 'src/app/store/restaurants/discount-codes/discount-codes.selectors';
import { DiscountCodeItem } from '../models/discount-code-item';

@Component({
  selector: 'app-discount-codes-list',
  templateUrl: './discount-codes-list.component.html',
  styleUrls: ['./discount-codes-list.component.scss'],
})
export class DiscountCodesListComponent implements OnInit {
  discountCodes$: Observable<DiscountCodeItem[]>;

  constructor(
    private store: Store
  ) { }

  ngOnInit(): void {
    this.discountCodes$ = this.store.select(selectDiscountCodes);
  }

  isGridComplete(discountCodes: DiscountCodeItem[]) {
    return discountCodes.length % 3 === 0;
  }
}
