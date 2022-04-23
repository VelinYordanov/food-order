import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { DiscountCodesListComponent } from './discount-codes-list.component';

describe('DiscountCodesListComponent', () => {
  let component: DiscountCodesListComponent;
  let fixture: ComponentFixture<DiscountCodesListComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ DiscountCodesListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DiscountCodesListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
