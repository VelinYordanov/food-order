import { Component, OnInit } from '@angular/core';
import { EMPTY, Observable } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { DiscountCode } from 'src/app/customers/models/discount-code';
import { AlertService } from 'src/app/shared/services/alert.service';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
import { DiscountCodeItem } from '../models/discount-code-item';
import { RestaurantService } from '../services/restaurant.service';

@Component({
  selector: 'app-discount-codes-list',
  templateUrl: './discount-codes-list.component.html',
  styleUrls: ['./discount-codes-list.component.scss'],
})
export class DiscountCodesListComponent implements OnInit {
  discountCodes$: Observable<DiscountCodeItem[]>;

  constructor(
    private authenticationService: AuthenticationService,
    private restaurantService: RestaurantService,
    private alertService: AlertService
  ) {}

  ngOnInit(): void {
    this.discountCodes$ = this.authenticationService.user$.pipe(
      switchMap(restaurant => 
        this.restaurantService.getDiscountCodes(restaurant.id)
        .pipe(
          catchError(error => {
            this.alertService.displayMessage(error?.error?.description || 'An error occurred while loading discount codes. Try again later.', 'error');
            return EMPTY;
          })
        ))
    )
  }

  isGridComplete(discountCodes: DiscountCodeItem[]) {
    return discountCodes.length % 3 === 0;
  }

  removeDiscountCode(discountCode: DiscountCode, discountCodes: DiscountCode[]) {
    const index = discountCodes.findIndex(code => code.id === discountCode.id);
    if(index !== -1) {
      discountCodes.splice(index, 1);
    }
  }
}
