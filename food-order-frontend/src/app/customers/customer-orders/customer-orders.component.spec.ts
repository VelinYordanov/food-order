import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CutomerOrdersComponent } from './customer-orders.component';

describe('CutomerOrdersComponent', () => {
  let component: CutomerOrdersComponent;
  let fixture: ComponentFixture<CutomerOrdersComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CutomerOrdersComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CutomerOrdersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
