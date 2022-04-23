import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { EditDiscountCodeComponent } from './edit-discount-code.component';

describe('EditDiscountCodeComponent', () => {
  let component: EditDiscountCodeComponent;
  let fixture: ComponentFixture<EditDiscountCodeComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ EditDiscountCodeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditDiscountCodeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
