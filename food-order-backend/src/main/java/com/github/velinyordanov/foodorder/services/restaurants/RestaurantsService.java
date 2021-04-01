package com.github.velinyordanov.foodorder.services.restaurants;

import java.util.Collection;

import com.github.velinyordanov.foodorder.dto.RestaurantDataDto;
import com.github.velinyordanov.foodorder.dto.RestaurantDto;
import com.github.velinyordanov.foodorder.dto.RestaurantEditDto;

public interface RestaurantsService {
	Collection<RestaurantDto> getAll();

	RestaurantDataDto getRestaurantData(String restaurantId);

	RestaurantDataDto editRestaurant(String restaurantId, RestaurantEditDto restaurantEditDto);
}
