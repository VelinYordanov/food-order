import { Category } from "./category";

export interface Food {
    id: string;
    name: string;
    description: string;
    categories: Category[];
    price: number;
}