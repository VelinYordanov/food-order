import { DatePipe } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { ChartDataSets } from 'chart.js';
import { Color, Label } from 'ng2-charts';
import { EMPTY } from 'rxjs';
import { catchError, map, switchMap, tap, withLatestFrom } from 'rxjs/operators';
import { AlertService } from 'src/app/shared/services/alert.service';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
import { RestaurantService } from '../services/restaurant.service';

const MY_FORMATS = {
  display: {
    dateInput: 'MM.yyyy',
  },
};

@Component({
  selector: 'app-monthly-graph',
  templateUrl: './monthly-graph.component.html',
  styleUrls: ['./monthly-graph.component.scss'],
  providers:[DatePipe, { provide: MAT_DATE_FORMATS, useValue: MY_FORMATS }]
})
export class MonthlyGraphComponent implements OnInit {
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

  constructor(
    private authenticationService: AuthenticationService,
    private restaurantService: RestaurantService,
    private alertService: AlertService,
    private datePipe: DatePipe
  ) {}

  ngOnInit(): void {
    this.date.valueChanges
    .pipe(
        withLatestFrom(this.authenticationService.user$),
        map(([date, restaurant]) => [date.month() + 1, date.year(), restaurant]),
        switchMap(([month, year, restaurant]) =>
          this.restaurantService.getMonthyGraphData(restaurant.id, month, year)
          .pipe(
            catchError((error) => {
              this.alertService.displayMessage(
                error?.error?.description ||
                  'An error occurred while loading monthly graph. Try again later.',
                'error'
              );
              return EMPTY;
            }),
            tap(_ => this.lineChartData[0].label = this.getLabel(month, year)),
          )
        )
      )
      .subscribe(graphData => {
        this.lineChartLabels = graphData.map(data => this.datePipe.transform(data.x, 'dd.MM.yyyy'));
        this.lineChartData[0].data = graphData.map(data => data.y);
      });
  }

  getLabel(month: number, year: number) {
    return `Monthly Graph for ${month}.${year}`;
  }

  onMonthSelected(date: Date, datepicker) {
    this.date.setValue(date);
    datepicker.close();
  }
}
