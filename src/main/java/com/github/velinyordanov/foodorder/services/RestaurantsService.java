package com.github.velinyordanov.foodorder.services;

import java.util.Collection;
import java.util.Optional;

import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.dto.RestaurantDto;
import com.github.velinyordanov.foodorder.dto.RestaurantRegisterDto;
import com.github.velinyordanov.foodorder.dto.UserDto;

public interface RestaurantsService {
    Collection<RestaurantDto> getAll();

    String register(RestaurantRegisterDto user);

    String login(UserDto user);

    Optional<Restaurant> findById(String id);
}
