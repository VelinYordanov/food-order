package com.github.velinyordanov.foodorder.controllers;

import java.util.Collection;

import javax.validation.Valid;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.dto.FoodCreateDto;
import com.github.velinyordanov.foodorder.dto.RestaurantDto;
import com.github.velinyordanov.foodorder.dto.RestaurantRegisterDto;
import com.github.velinyordanov.foodorder.dto.UserDto;
import com.github.velinyordanov.foodorder.services.JwtTokenService;
import com.github.velinyordanov.foodorder.services.RestaurantsService;

@RestController
@RequestMapping("restaurants")
public class RestaurantsController {
    private final RestaurantsService restaurantsService;

    public RestaurantsController(RestaurantsService restaurantsService, JwtTokenService jwtTokenService) {
	this.restaurantsService = restaurantsService;
    }

    @GetMapping()
    @Secured("ROLE_CUSTOMER")
    public Collection<RestaurantDto> getAll() {
	return this.restaurantsService.getAll();
    }

    @PostMapping()
    public String register(@Valid @RequestBody RestaurantRegisterDto userDto) {
	return this.restaurantsService.register(userDto);
    }

    @PostMapping("tokens")
    public String login(@Valid @RequestBody UserDto user) {
	return this.restaurantsService.login(user);
    }

    @PostMapping("foods")
    @Secured("ROLE_RESTAURANT")
    public void addFoodToRestaurant(@AuthenticationPrincipal Restaurant restaurant,
	    @RequestBody FoodCreateDto foodCreateDto) {
	this.restaurantsService.addFoodsToRestaurant(restaurant.getId(), foodCreateDto);
    }
}
