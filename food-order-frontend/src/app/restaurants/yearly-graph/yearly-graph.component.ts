import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { ChartDataSets, ChartOptions } from 'chart.js';
import { Color, Label } from 'ng2-charts';
import { EMPTY, ReplaySubject, Subject } from 'rxjs';
import {
  catchError,
  map,
  switchMap,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { AlertService } from 'src/app/shared/services/alert.service';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
import { RestaurantService } from '../services/restaurant.service';

const MY_FORMATS = {
  display: {
    dateInput: 'yyyy',
  },
};

@Component({
  selector: 'app-yearly-graph',
  templateUrl: './yearly-graph.component.html',
  styleUrls: ['./yearly-graph.component.scss'],
  providers: [{ provide: MAT_DATE_FORMATS, useValue: MY_FORMATS }],
})
export class YearlyGraphComponent implements OnInit, OnDestroy {
  @Input('year') set year(value: number) {
    if (value) {
      this.yearChanges$.next(value);
    }
  }

  public date: FormControl = new FormControl();

  public lineChartData: ChartDataSets[] = [{ data: [], label: '' }];

  public lineChartLabels: Label[] = [];

  public lineChartOptions = {
    responsive: true,
  };

  public lineChartColors: Color[] = [
    {
      borderColor: 'black',
      backgroundColor: 'rgba(255,0,0,0.3)',
    },
  ];

  public lineChartLegend = true;
  public lineChartType = 'line';
  public lineChartPlugins = [];

  private yearChanges$ = new ReplaySubject<number>(1);

  constructor(
    private authenticationService: AuthenticationService,
    private restaurantService: RestaurantService,
    private alertService: AlertService
  ) {}

  ngOnInit(): void {
    this.date.valueChanges
      .pipe(
        map((date) => date.year()),
        withLatestFrom(this.authenticationService.user$),
        switchMap(([year, restaurant]) =>
          this.restaurantService.getYearlyGraph(restaurant.id, year).pipe(
            catchError((error) => {
              this.alertService.displayMessage(
                error?.error?.description ||
                  'An error occurred while loading yearly graph. Try again later.',
                'error'
              );
              return EMPTY;
            }),
            tap((_) => (this.lineChartData[0].label = this.getLabel(year)))
          )
        )
      )
      .subscribe((graphData) => {
        this.lineChartLabels = graphData.map((data) => data.x);
        this.lineChartData[0].data = graphData.map((data) => data.y);
      });
  }

  ngOnDestroy(): void {
    this.yearChanges$.complete();
  }

  getLabel(year: number) {
    return `Yearly Graph for ${year}`;
  }

  onYearSelected(date: Date, datepicker) {
    this.date.setValue(date);
    datepicker.close();
  }
}
