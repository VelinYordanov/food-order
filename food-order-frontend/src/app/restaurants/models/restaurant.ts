import { Category } from "./category";
import { Food } from "./food";

export interface Restaurant {
    id: string;
    name: string;
    description: string;
    categories: Category[];
    foods: Food[];
}