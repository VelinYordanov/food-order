import { GraphData } from "src/app/restaurants/models/graph-data";

export interface GraphStateData<TKey> {
    [key: string]: GraphData<TKey, number>[]
}