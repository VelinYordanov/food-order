package com.github.velinyordanov.foodorder.services.restaurants.impl;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.github.velinyordanov.foodorder.data.FoodOrderData;
import com.github.velinyordanov.foodorder.dto.FoodDto;
import com.github.velinyordanov.foodorder.dto.RestaurantDataDto;
import com.github.velinyordanov.foodorder.dto.RestaurantDto;
import com.github.velinyordanov.foodorder.dto.RestaurantEditDto;
import com.github.velinyordanov.foodorder.exceptions.NotFoundException;
import com.github.velinyordanov.foodorder.mapping.Mapper;
import com.github.velinyordanov.foodorder.services.restaurants.RestaurantsService;

@Service
public class RestaurantsServiceImpl implements RestaurantsService {
	private final FoodOrderData foodOrderData;
	private final Mapper mapper;

	public RestaurantsServiceImpl(FoodOrderData foodOrderData, Mapper mapper) {
		this.foodOrderData = foodOrderData;
		this.mapper = mapper;
	}

	@Override
	public Collection<RestaurantDto> getAll() {
		return this.foodOrderData.restaurants().getRestaurantsList();
	}

	@Override
	public RestaurantDataDto getRestaurantData(String restaurantId) {
		return this.foodOrderData.restaurants()
				.findById(restaurantId)
				.stream()
				.map(restaurant -> this.mapper.map(restaurant, RestaurantDataDto.class))
				.peek(restaurant -> restaurant.setFoods(
						this.foodOrderData.foods()
								.findByRestaurantId(restaurantId)
								.stream()
								.map(food -> this.mapper.map(food, FoodDto.class))
								.collect(Collectors.toList())))
				.findAny()
				.orElseThrow(() -> new NotFoundException(
						MessageFormat.format("Restaurant with id {0} not found", restaurantId)));
	}

	@Override
	public RestaurantDataDto editRestaurant(String restaurantId, RestaurantEditDto restaurantEditDto) {
		return this.foodOrderData.restaurants()
				.findById(restaurantId)
				.map(restaurant -> {
					restaurant.setName(restaurantEditDto.getName());
					restaurant.setDescription(restaurantEditDto.getDescription());

					RestaurantDataDto restaurantDataDto = this.mapper.map(
							this.foodOrderData.restaurants().save(restaurant),
							RestaurantDataDto.class);
					restaurantDataDto.setFoods(
							this.foodOrderData.foods()
									.findByRestaurantId(restaurantId)
									.stream()
									.map(food -> this.mapper.map(food, FoodDto.class))
									.collect(Collectors.toList()));

					return restaurantDataDto;
				}).orElseThrow(
						() -> new NotFoundException(
								MessageFormat.format("Restaurant with id {0} not found!", restaurantId)));
	}
}
