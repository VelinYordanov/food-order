import { EnumData } from "src/app/shared/models/enum-data";

export interface EnumStoreData {
    entities: EnumData[],
    isLoading: boolean,
}