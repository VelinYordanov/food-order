import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { GenerateDiscountCodeComponent } from './generate-discount-code.component';

describe('GenerateDiscountCodeComponent', () => {
  let component: GenerateDiscountCodeComponent;
  let fixture: ComponentFixture<GenerateDiscountCodeComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ GenerateDiscountCodeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GenerateDiscountCodeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
