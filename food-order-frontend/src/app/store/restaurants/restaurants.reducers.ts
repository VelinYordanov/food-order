import { createReducer, on } from "@ngrx/store";
import { Order } from "src/app/customers/models/order";
import { Food } from "src/app/restaurants/models/food";
import { Page } from "src/app/shared/models/page";
import { OrderStatusChangePayload } from "../models/order-status-change-payload";
import { RestaurantsState } from "../models/restaurants-state";
import { createDiscountCodeSuccessAction, deleteDiscountCodeSuccessAction, loadDiscountCodesSuccesAction } from "./discount-codes/discount-codes.actions";
import { discountCodesReducer } from "./discount-codes/discount-codes.reducers";
import { loadMonthlyGraphSuccesAction, loadYearlyGraphSuccesAction } from "./graphs/graphs.actions";
import { graphsReducer } from "./graphs/graphs.reducers";
import { addCategoryToRestaurantSuccessAction, addFoodToRestaurantSuccessAction, deleteCategoryFromRestaurantSuccessAction, deleteFoodFromRestaurantAction, deleteFoodFromRestaurantSuccessAction, editRestaurantFoodAction, editRestaurantFoodSuccessAction, editRestaurantSuccessAction, loadRestaurantErrorAction, loadRestaurantOrdersSuccessAction, loadRestaurantsSuccessAction, loadRestaurantSuccessAction, updateRestaurantOrderSuccessAction } from "./restaurants.actions";

const initialState: RestaurantsState = {
    restaurants: [],
    currentRestaurant: null,
    discountCodes: [],
    graphData: {
        monthlyGraphData: {},
        yearlyGraphData: {}
    },
    orders: {
        orders: {},
        totalPages: 0,
        totalElements: 0,
        last: true,
        numberOfElements: 0,
        first: true,
        number: 0,
        size: 0
    }
}
export const restaurantsReducer = createReducer(
    initialState,
    on(loadRestaurantsSuccessAction, (state, action) => ({ ...state, ...{ restaurants: action.payload } })),
    on(loadRestaurantSuccessAction, editRestaurantSuccessAction, (state, action) => ({ ...state, ...{ currentRestaurant: action.payload } })),
    on(loadRestaurantErrorAction, (state, action) => ({ ...state, ...{ currentRestaurant: null } })),
    on(deleteDiscountCodeSuccessAction, loadDiscountCodesSuccesAction, createDiscountCodeSuccessAction, (state, action) => ({ ...state, ...{ discountCodes: discountCodesReducer(state.discountCodes, action) } })),
    on(loadMonthlyGraphSuccesAction,
        loadYearlyGraphSuccesAction,
        (state, action) => ({ ...state, graphData: graphsReducer(state.graphData, action) })),
    on(addFoodToRestaurantSuccessAction, (state, action) => ({ ...state, currentRestaurant: { ...state.currentRestaurant, foods: [...state.currentRestaurant.foods, action.payload] } })),
    on(deleteFoodFromRestaurantSuccessAction, (state, action) => ({ ...state, currentRestaurant: { ...state.currentRestaurant, foods: state.currentRestaurant.foods.filter(f => f.id !== action.payload.data) } })),
    on(editRestaurantFoodSuccessAction, (state, action) => editFood(state, action)),
    on(addCategoryToRestaurantSuccessAction, (state, action) => ({ ...state, currentRestaurant: { ...state.currentRestaurant, categories: [...state.currentRestaurant.categories, action.payload.data] } })),
    on(deleteCategoryFromRestaurantSuccessAction, (state, action) => ({ ...state, currentRestaurant: { ...state.currentRestaurant, categories: state.currentRestaurant.categories.filter(c => c.id !== action.payload.id) } })),
    on(loadRestaurantOrdersSuccessAction, (state, action) => loadRestaurantOrders(state, action)),
    on(updateRestaurantOrderSuccessAction, (state, action) => updateRestaurantOrderStatus(state, action))
)

function editFood(state: RestaurantsState, action: { payload: Food }): RestaurantsState {
    const food = action.payload;
    const updatedFoods = [...state.currentRestaurant.foods.filter(f => f.id !== food.id), food];
    return { ...state, currentRestaurant: { ...state.currentRestaurant, foods: updatedFoods } };
}

function loadRestaurantOrders(state: RestaurantsState, action: { payload: Page<Order> }): RestaurantsState {
    const page = action.payload;
    const orders = { ...state.orders.orders };
    orders[page.number] = page.content;

    return {
        ...state, orders: { ...state.orders, ...page, orders: orders }
    }
}

function updateRestaurantOrderStatus(state: RestaurantsState, action: { payload: OrderStatusChangePayload }): RestaurantsState {
    const orderPage = Object.getOwnPropertyNames(state.orders.orders)
        .find(i => state.orders.orders[i].find(o => o.id === action.payload.orderId))

    const orders = state.orders.orders[orderPage];
    const orderIndex = orders.findIndex(o => o.id === action.payload.orderId);
    const order = orders[orderIndex];
    const newOrder = { ...order, status: action.payload.orderStatus.status };
    const newOrders = orders.slice();
    newOrders[orderIndex] = newOrder;
    const restaurantOrders = { ...state.orders.orders };
    restaurantOrders[orderPage] = newOrders;

    return {
        ...state, orders: { ...state.orders, orders: restaurantOrders }
    }
}

export const restaurantsStateKey = 'restaurants';