package com.github.velinyordanov.foodorder.services.restaurants;

import com.github.velinyordanov.foodorder.dto.FoodCreateDto;
import com.github.velinyordanov.foodorder.dto.FoodDto;

public interface RestaurantsFoodsService {
	FoodDto addFoodToRestaurant(String restaurantId, FoodCreateDto foodCreateDto);

	FoodDto editFood(String restaurantId, String foodId, FoodCreateDto foodCreateDto);

	void deleteFood(String restaurantId, String foodId);
}
