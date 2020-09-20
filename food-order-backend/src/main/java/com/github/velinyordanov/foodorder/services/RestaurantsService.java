package com.github.velinyordanov.foodorder.services;

import java.util.Collection;
import java.util.Optional;

import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.dto.CategoryCreateDto;
import com.github.velinyordanov.foodorder.dto.CategoryDto;
import com.github.velinyordanov.foodorder.dto.FoodCreateDto;
import com.github.velinyordanov.foodorder.dto.FoodDto;
import com.github.velinyordanov.foodorder.dto.OrderDto;
import com.github.velinyordanov.foodorder.dto.OrderListDto;
import com.github.velinyordanov.foodorder.dto.RestaurantDataDto;
import com.github.velinyordanov.foodorder.dto.RestaurantDto;
import com.github.velinyordanov.foodorder.dto.RestaurantEditDto;
import com.github.velinyordanov.foodorder.dto.RestaurantRegisterDto;
import com.github.velinyordanov.foodorder.dto.UserDto;

public interface RestaurantsService {
    Collection<RestaurantDto> getAll();

    String register(RestaurantRegisterDto user);

    String login(UserDto user);

    Optional<Restaurant> findById(String id);

    FoodDto addFoodToRestaurant(String restaurantId, FoodCreateDto foodCreateDto);

    FoodDto editFood(String restaurantId, String foodId, FoodCreateDto foodCreateDto);

    RestaurantDataDto editRestaurant(String restaurantId, RestaurantEditDto restaurantEditDto);

    void deleteCategory(String restaurantId, String categoryId);

    void deleteFood(String restaurantId, String foodId);

    Optional<CategoryDto> addCategoryForRestaurant(String restaurantId, CategoryCreateDto categoryCreateDto);

    RestaurantDataDto getRestaurantData(String restaurantId);

    Collection<CategoryDto> getCategoriesForRestaurant(String restaurantId);

    Collection<OrderListDto> getRestaurantOrders(String restaurantId);

    OrderDto getRestaurantOrder(String restaurantId, String orderId);
}
