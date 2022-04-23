import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { YearlyGraphComponent } from './yearly-graph.component';

describe('YearlyGraphComponent', () => {
  let component: YearlyGraphComponent;
  let fixture: ComponentFixture<YearlyGraphComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ YearlyGraphComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(YearlyGraphComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
