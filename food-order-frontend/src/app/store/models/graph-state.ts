import { GraphStateData } from "./graph-state-data";

export interface GraphState {
    monthlyGraphData: GraphStateData<string>,
    yearlyGraphData: GraphStateData<string>
}