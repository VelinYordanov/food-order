import { createReducer, on } from "@ngrx/store";
import { CartFood } from "src/app/restaurants/models/cart-food";
import { CartItem } from "src/app/restaurants/models/cart-item";
import { addFoodToCartAction, decreaseFoodQuantityAction, increaseFoodQuantityAction, loadCartAction, removeFoodFromCartAction } from "./cart.actions";

const initialState: CartItem[] = [];

export const cartSelectedFoodsReducer = createReducer(
    initialState,
    on(addFoodToCartAction, (state, action) => addFoodToCart(state, action.payload)),
    on(removeFoodFromCartAction, (state, action) => state.filter(food => food.food.id !== action.payload.food.id)),
    on(increaseFoodQuantityAction, (state, action) => modifyCount(state, action.payload, CountModification.Increment)),
    on(decreaseFoodQuantityAction, (state, action) => modifyCount(state, action.payload, CountModification.Decrement)),
    on(loadCartAction, (state, action) => ([...state, ...action.payload]))
)

function modifyCount(items: CartItem[], item: CartFood, countModification: CountModification): CartItem[] {
    return items.reduce((acc, current) => {
        if (current.food.id === item.id) {
            const newQuantity = countModification === CountModification.Increment ? (current.quantity + 1) : (current.quantity - 1);
            if (newQuantity <= 0) {
                return acc;
            }

            acc.push({ ...current, quantity: newQuantity });
        } else {
            acc.push(current);
        }

        return acc;
    }, []);
}

function addFoodToCart(foods: CartItem[], food: CartFood) {
    if (foods.find(f => f.food.id === food.id)) {
        return foods.map(item => {
            if (item.food.id === food.id) {
                return { quantity: item.quantity + 1, food };
            }

            return item;
        })
    }

    return [...foods, { quantity: 1, food }]
}

enum CountModification {
    Increment = 0,
    Decrement = 1
}