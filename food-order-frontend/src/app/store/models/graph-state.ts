import { GraphStateData } from "./graph-state-data";

export interface GraphState {
    monthlyGraphData: GraphStateData<Date>,
    yearlyGraphData: GraphStateData<string>
}