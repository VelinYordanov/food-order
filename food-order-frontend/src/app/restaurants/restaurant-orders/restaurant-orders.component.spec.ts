import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RestaurantOrdersComponent } from './restaurant-orders.component';

describe('RestaurantOrdersComponent', () => {
  let component: RestaurantOrdersComponent;
  let fixture: ComponentFixture<RestaurantOrdersComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ RestaurantOrdersComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RestaurantOrdersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
