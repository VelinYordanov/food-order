import { createReducer, on } from "@ngrx/store";
import { GraphState } from "../../models/graph-state";
import { GraphData } from '../../../restaurants/models/graph-data';
import { loadMonthlyGraphSuccesAction, loadYearlyGraphSuccesAction } from "./graphs.actions";
import { YearlyGraphPayload } from "../../models/yearly-graph-payload";

const initialState: GraphState = {
    yearlyGraphData: {},
    monthlyGraphData: {}
};

export const graphsReducer = createReducer(
    initialState,
    on(loadMonthlyGraphSuccesAction, (state, action) => addMonthlyGraphData(state, action)),
    on(loadYearlyGraphSuccesAction, (state, action) => addYearlyGraphData(state, action))
)

function addMonthlyGraphData(state: GraphState, action: { payload: GraphData<string, number>[] }) {
    const firstEntry = action.payload[0];
    const date = new Date(firstEntry.x);
    const key = `${date.getFullYear()}-${date.getMonth() + 1}`;

    return {
        ...state,
        monthlyGraphData: {
            ...state.monthlyGraphData,
            [key]: action.payload
        }
    };
}

function addYearlyGraphData(state: GraphState, { payload }: { payload: YearlyGraphPayload }) {
    const key = payload.year;

    return {
        ...state,
        yearlyGraphData: {
            ...state.yearlyGraphData,
            [key]: payload.graphData
        }
    };
}