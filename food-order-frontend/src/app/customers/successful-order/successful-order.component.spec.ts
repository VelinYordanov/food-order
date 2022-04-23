import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { SuccessfulOrderComponent } from './successful-order.component';

describe('SuccessfulOrderComponent', () => {
  let component: SuccessfulOrderComponent;
  let fixture: ComponentFixture<SuccessfulOrderComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ SuccessfulOrderComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SuccessfulOrderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
