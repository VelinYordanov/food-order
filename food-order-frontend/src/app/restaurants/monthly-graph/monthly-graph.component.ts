import { DatePipe } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { ChartDataSets } from 'chart.js';
import { Color, Label } from 'ng2-charts';
import { combineLatest, EMPTY, ReplaySubject } from 'rxjs';
import { catchError, switchMap, tap, withLatestFrom } from 'rxjs/operators';
import { AlertService } from 'src/app/shared/services/alert.service';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
import { RestaurantService } from '../services/restaurant.service';

@Component({
  selector: 'app-monthly-graph',
  templateUrl: './monthly-graph.component.html',
  styleUrls: ['./monthly-graph.component.scss'],
  providers:[DatePipe]
})
export class MonthlyGraphComponent implements OnInit {
  @Input('year') set year(value: number) {
    if (value) {
      this.yearChanges$.next(value);
    }
  }

  @Input('month') set month(value: number) {
    if (value) {
      this.monthChanges$.next(value);
    }
  }

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
  private monthChanges$ = new ReplaySubject<number>(1);

  constructor(
    private authenticationService: AuthenticationService,
    private restaurantService: RestaurantService,
    private alertService: AlertService,
    private datePipe: DatePipe
  ) {}

  ngOnInit(): void {
    combineLatest(
      [
        this.yearChanges$,
        this.monthChanges$
      ]
    ).pipe(
        withLatestFrom(this.authenticationService.user$),
        switchMap(([[year, month], restaurant]) =>
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

  ngOnDestroy(): void {
    this.yearChanges$.complete();
    this.monthChanges$.complete();
  }

  getLabel(month: number, year: number) {
    return `Monthly Graph for ${month}.${year}`;
  }
}
