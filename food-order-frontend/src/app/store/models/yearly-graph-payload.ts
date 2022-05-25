import { GraphData } from "src/app/restaurants/models/graph-data";

export interface YearlyGraphPayload {
    year: number,
    graphData: GraphData<string, number>[]
}