import { createFeatureSelector, createSelector } from "@ngrx/store";
import { loggedInUserIdSelector } from "src/app/store/authentication/authentication.selectors";
import { CustomersState } from "../../models/customers-state";
import { customersStateKey } from "../customers.reducer";

const customersStateSelector = createFeatureSelector<CustomersState>(customersStateKey);

export const cartStateSelector = createSelector(
    customersStateSelector,
    state => state.cart
);

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

export const selectDiscountCode = createSelector(
    cartStateSelector,
    state => state.selectedDiscountCode
)

export const selectDiscountCodeId = createSelector(
    selectDiscountCode,
    state => state.id
)

export const orderItemsSelector = createSelector(
    loggedInUserIdSelector,
    selectedRestaurantIdSelector,
    selectedAddressIdSelector,
    selectedItemsAsOrderFoodSelector,
    selectDiscountCodeId,
    (customerId, restaurantId, addressId, foods, discountCodeId) => ({ customerId, restaurantId, addressId, foods, discountCodeId })
);