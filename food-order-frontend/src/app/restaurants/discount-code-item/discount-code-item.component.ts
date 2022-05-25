import {
  Component,
  Input,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { filter } from 'rxjs/operators';
import { deleteDiscountCodePromptAction } from 'src/app/store/restaurants/discount-codes/discount-codes.actions';
import { EditDiscountCodeComponent } from '../edit-discount-code/edit-discount-code.component';
import { DiscountCodeItem } from '../models/discount-code-item';

@Component({
  selector: 'app-discount-code-item',
  templateUrl: './discount-code-item.component.html',
  styleUrls: ['./discount-code-item.component.scss'],
})
export class DiscountCodeItemComponent {
  @Input() discountCode: DiscountCodeItem;

  constructor(
    private store: Store,
    public dialog: MatDialog
  ) { }

  openEditDialog(): void {
    this.dialog.open(EditDiscountCodeComponent, {
      data: this.discountCode,
      autoFocus: false,
    });
  }

  delete() {
    this.store.dispatch(deleteDiscountCodePromptAction({ payload: this.discountCode }));
  }
}
