import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { Subject } from 'rxjs';
import { filter, withLatestFrom } from 'rxjs/operators';
import { DiscountCode } from 'src/app/customers/models/discount-code';
import { loggedInUserIdSelector } from 'src/app/store/authentication/authentication.selectors';
import { deleteDiscountCodeAction, deleteDiscountCodePromptAction } from 'src/app/store/restaurants/discount-codes/discount-codes.actions';
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
    const dialogRef = this.dialog.open(EditDiscountCodeComponent, {
      data: this.discountCode,
      autoFocus: false,
    });

    dialogRef.afterClosed()
      .pipe(
        filter(x => !!x)
      ).subscribe((edittedDiscountCode) => {
        this.discountCode = edittedDiscountCode;
      });
  }

  delete() {
    this.store.dispatch(deleteDiscountCodePromptAction({ payload: this.discountCode }));
  }
}
