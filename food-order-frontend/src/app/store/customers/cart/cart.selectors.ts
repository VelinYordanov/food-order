import { createFeatureSelector, createSelector } from "@ngrx/store";
import { loggedInUserIdSelector } from "src/app/store/authentication/authentication.selectors";
import { CartState } from "../../models/cart-state";

const cartStateSelector = createFeatureSelector<CartState>('cart');

export const selectedItemsSelector = createSelector(
    cartStateSelector,
    state => state.selectedItems
);

export const cartItemsSumSelector = createSelector(
    selectedItemsSelector,
    state => state.reduce((sum, current) => sum + current.quantity, 0)
);

export const selectedAddressSelector = createSelector(
    cartStateSelector,
    state => state.selectedAddress
);

export const selectedAddressIdSelector = createSelector(
    selectedAddressSelector,
    address => address.id
);

export const selectedRestaurantSelector = createSelector(
    cartStateSelector,
    state => state.selectedRestaurant
);

export const selectedRestaurantIdSelector = createSelector(
    selectedRestaurantSelector,
    restaurant => restaurant.id
);

export const selectedItemsAsOrderFoodSelector = createSelector(
    selectedItemsSelector,
    state => state.map(item => ({
        id: item.food.id,
        quantity: item.quantity,
    }))
);

export const orderItemsSelector = createSelector(
    loggedInUserIdSelector,
    selectedRestaurantIdSelector,
    selectedAddressIdSelector,
    selectedItemsAsOrderFoodSelector,
    (customerId, restaurantId, addressId, foods) => ({ customerId, restaurantId, addressId, foods })
);