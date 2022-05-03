import { createReducer, on } from "@ngrx/store";
import { CartState } from "../../models/cart-state";
import { cartSelectedFoodsReducer } from "./cart-selected-foods.reducers";
import { addFoodToCartAction, clearCartAction, decreaseFoodQuantityAction, increaseFoodQuantityAction, loadCartAction, removeFoodFromCartAction, selectAddressAction, selectRestaurantAction } from "./cart.actions";

const initialState: CartState = {
    selectedRestaurant: null,
    selectedAddress: null,
    selectedItems: []
};

export const cartReducer = createReducer(
    initialState,
    on(selectRestaurantAction, (state, action) => ({ ...state, ...{ selectedRestaurant: action.payload } })),
    on(selectAddressAction, (state, action) => ({ ...state, ...{ selectedAddress: action.payload } })),
    on(clearCartAction, (state, action) => initialState),
    on(
        addFoodToCartAction,
        increaseFoodQuantityAction,
        decreaseFoodQuantityAction,
        removeFoodFromCartAction,
        loadCartAction,
        (state, action) => ({ ...state, ...{ selectedItems: cartSelectedFoodsReducer(state.selectedItems, action) } }))
)