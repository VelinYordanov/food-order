import { DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { Actions } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { ChartDataSets } from 'chart.js';
import { Color, Label } from 'ng2-charts';
import { map, switchMap, tap, withLatestFrom } from 'rxjs/operators';
import { loggedInUserIdSelector } from 'src/app/store/authentication/authentication.selectors';
import { loadMonthlyGraphAction } from 'src/app/store/restaurants/graphs/graphs.actions';
import { selectMonthlyGraphData } from 'src/app/store/restaurants/graphs/graphs.selectors';

const MY_FORMATS = {
  display: {
    dateInput: 'MM.yyyy',
  },
};

@Component({
  selector: 'app-monthly-graph',
  templateUrl: './monthly-graph.component.html',
  styleUrls: ['./monthly-graph.component.scss'],
  providers: [DatePipe, { provide: MAT_DATE_FORMATS, useValue: MY_FORMATS }]
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
    private store: Store,
    private actions$: Actions,
    private datePipe: DatePipe
  ) { }

  ngOnInit(): void {
    this.date.valueChanges
      .pipe(
        switchMap(date =>
          this.store.select(selectMonthlyGraphData(date.year(), date.month() + 1))
            .pipe(
              map(data => [date.year(), date.month() + 1, data])
            ))
      ).subscribe(([year, month, graphData]) => {
        this.lineChartData[0].label = this.getLabel(month, year);
        this.lineChartLabels = graphData.map(data => this.datePipe.transform(data.x, 'dd.MM.yyyy'));
        this.lineChartData[0].data = graphData.map(data => data.y);
      });

    this.date.valueChanges
      .pipe(
        withLatestFrom(this.store.select(loggedInUserIdSelector)),
        map(([date, restaurantId]) => [date.month() + 1, date.year(), restaurantId])
      )
      .subscribe(([month, year, restaurantId]) => {
        const payload = { restaurantId, month, year };
        this.store.dispatch(loadMonthlyGraphAction({ payload }));
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
