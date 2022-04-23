import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { MonthlyGraphComponent } from './monthly-graph.component';

describe('MonthlyGraphComponent', () => {
  let component: MonthlyGraphComponent;
  let fixture: ComponentFixture<MonthlyGraphComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ MonthlyGraphComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MonthlyGraphComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
