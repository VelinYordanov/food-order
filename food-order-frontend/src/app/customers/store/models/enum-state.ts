import { EnumData } from "src/app/shared/models/enum-data";

export interface EnumState {
    cities: EnumData[];
    addressTypes: EnumData[];
    orderTypes: EnumData[];
}