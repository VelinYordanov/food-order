import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { DiscountCodeItemComponent } from './discount-code-item.component';

describe('DiscountCodeItemComponent', () => {
  let component: DiscountCodeItemComponent;
  let fixture: ComponentFixture<DiscountCodeItemComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ DiscountCodeItemComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DiscountCodeItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
