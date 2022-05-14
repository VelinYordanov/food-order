import { createReducer, on } from "@ngrx/store";
import { GraphState } from "../../models/graph-state";
import { GraphData } from '../../../restaurants/models/graph-data';
import { loadMonthlyGraphSuccesAction, loadYearlyGraphSuccesAction } from "./graphs.actions";

const initialState: GraphState = {
    yearlyGraphData: {},
    monthlyGraphData: {}
};

export const graphsReducer = createReducer(
    initialState,
    on(loadMonthlyGraphSuccesAction, (state, action) => addMonthlyGraphData(state, action)),
    on(loadYearlyGraphSuccesAction, (state, action) => addYearlyGraphData(state, action))
)

function addMonthlyGraphData(state: GraphState, { payload }: { payload: GraphData<Date, number>[] }) {
    const firstEntry = payload[0];
    const key = `${firstEntry.x.getFullYear()}-${firstEntry.x.getMonth() + 1}`;

    return {
        ...state,
        monthlyGraphData: {
            ...state.monthlyGraphData,
            [key]: payload
        }
    };
}

function addYearlyGraphData(state: GraphState, { payload }: { payload: GraphData<string, number>[] }) {
    const firstEntry = payload[0];
    const key = firstEntry.x;

    return {
        ...state,
        yearlyGraphData: {
            ...state.yearlyGraphData,
            [key]: payload
        }
    };
}