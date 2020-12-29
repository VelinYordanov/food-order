import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DiscountCodeItemComponent } from './discount-code-item.component';

describe('DiscountCodeItemComponent', () => {
  let component: DiscountCodeItemComponent;
  let fixture: ComponentFixture<DiscountCodeItemComponent>;

  beforeEach(async(() => {
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
