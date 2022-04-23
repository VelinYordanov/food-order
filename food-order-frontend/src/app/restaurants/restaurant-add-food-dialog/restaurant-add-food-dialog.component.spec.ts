import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RestaurantAddFoodDialogComponent } from './restaurant-add-food-dialog.component';

describe('RestaurantAddFoodDialogComponent', () => {
  let component: RestaurantAddFoodDialogComponent;
  let fixture: ComponentFixture<RestaurantAddFoodDialogComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ RestaurantAddFoodDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RestaurantAddFoodDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
