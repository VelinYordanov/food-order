import { createAction, props } from "@ngrx/store";
import { GraphData } from "src/app/restaurants/models/graph-data";
import { MonthlyGraphState } from "../../models/monthly-graph-payload";
import { YearlyGraphState } from "../../models/yearly-graph-state";

export const loadMonthlyGraphAction = createAction('[Graphs] Load Monthly Graph', props<{ payload: MonthlyGraphState }>());
export const loadMonthlyGraphSuccesAction = createAction('[Graphs] Load Monthly Graph Success', props<{ payload: GraphData<Date, number>[] }>());
export const loadMonthlyGraphErrorAction = createAction('[Graphs] Load Monthly Graph Error', props<{ payload: any }>());

export const loadYearlyGraphAction = createAction('[Graphs] Load Yearly Graph', props<{ payload: YearlyGraphState }>());
export const loadYearlyGraphSuccesAction = createAction('[Graphs] Load Yearly Graph Success', props<{ payload: GraphData<string, number>[] }>());
export const loadYearlyGraphErrorAction = createAction('[Graphs] Load Yearly Graph Error', props<{ payload: any }>());