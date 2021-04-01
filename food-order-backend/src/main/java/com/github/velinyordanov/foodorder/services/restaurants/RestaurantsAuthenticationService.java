package com.github.velinyordanov.foodorder.services.restaurants;

import java.util.Optional;

import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.dto.RestaurantRegisterDto;
import com.github.velinyordanov.foodorder.dto.UserLoginDto;

public interface RestaurantsAuthenticationService {
	String register(RestaurantRegisterDto user);

	String login(UserLoginDto user);

	Optional<Restaurant> findById(String id);
}
