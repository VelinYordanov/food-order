import { Component, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { Store } from '@ngrx/store';
import { ChartDataSets } from 'chart.js';
import { Color, Label } from 'ng2-charts';
import {
  map,
  switchMap,
  withLatestFrom,
} from 'rxjs/operators';
import { loggedInUserIdSelector } from 'src/app/store/authentication/authentication.selectors';
import { loadYearlyGraphAction } from 'src/app/store/restaurants/graphs/graphs.actions';
import { selectYearlyGraphData } from 'src/app/store/restaurants/graphs/graphs.selectors';

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
export class YearlyGraphComponent implements OnInit {
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
  ) { }

  ngOnInit(): void {
    this.date.valueChanges
      .pipe(
        map((date) => date.year()),
        withLatestFrom(this.store.select(loggedInUserIdSelector)))
      .subscribe(([year, restaurantId]) => {
        const payload = { restaurantId, year };
        this.store.dispatch(loadYearlyGraphAction({ payload }));
      });

    this.date.valueChanges
      .pipe(
        map(date => date.year()),
        switchMap(year => this.store.select(selectYearlyGraphData(year))
          .pipe(
            map(graphData => [year, graphData])
          )
        )).subscribe(([year, graphData]) => {
          this.lineChartData[0].label = this.getLabel(year);
          this.lineChartLabels = graphData.map((data) => data.x);
          this.lineChartData[0].data = graphData.map((data) => data.y);
        })
  }

  getLabel(year: number) {
    return `Yearly Graph for ${year}`;
  }

  onYearSelected(date: Date, datepicker) {
    this.date.setValue(date);
    datepicker.close();
  }
}
