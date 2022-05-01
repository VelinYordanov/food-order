import { createReducer, on } from "@ngrx/store";
import { CartFood } from "src/app/restaurants/models/cart-food";
import { CartItem } from "src/app/restaurants/models/cart-item";
import { addFoodToCartAction, decreaseFoodQuantityAction, increaseFoodQuantityAction, loadCartAction, removeFoodFromCartAction } from "./cart.actions";

const initialState: CartItem[] = [];

export const cartSelectedFoodsReducer = createReducer(
    initialState,
    on(addFoodToCartAction, (state, action) => ([...state, action.payload])),
    on(removeFoodFromCartAction, (state, action) => state.filter(food => food.food.id !== action.payload.food.id)),
    on(increaseFoodQuantityAction, (state, action) => modifyCount(state, action.payload, CountModification.Increment)),
    on(decreaseFoodQuantityAction, (state, action) => modifyCount(state, action.payload, CountModification.Decrement)),
    on(loadCartAction, (state, action) => ([...state, ...action.payload]))
)

function modifyCount(items: CartItem[], item: CartFood, countModification: CountModification): CartItem[] {
    return items.map(x => {
        if (x.food.id === item.id) {
            return { ...x, ...{ quantity: (countModification === CountModification.Increment ? (x.quantity + 1) : (x.quantity - 1)) } }
        }

        return x;
    })
}

enum CountModification {
    Increment = 0,
    Decrement = 1
}