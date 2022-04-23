import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RestaurantFoodComponent } from './restaurant-food.component';

describe('RestaurantFoodComponent', () => {
  let component: RestaurantFoodComponent;
  let fixture: ComponentFixture<RestaurantFoodComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ RestaurantFoodComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RestaurantFoodComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
