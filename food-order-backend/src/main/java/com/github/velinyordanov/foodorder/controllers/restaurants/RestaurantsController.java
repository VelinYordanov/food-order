package com.github.velinyordanov.foodorder.controllers.restaurants;

import java.util.Collection;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.velinyordanov.foodorder.dto.RestaurantDataDto;
import com.github.velinyordanov.foodorder.dto.RestaurantDto;
import com.github.velinyordanov.foodorder.dto.RestaurantEditDto;
import com.github.velinyordanov.foodorder.services.restaurants.RestaurantsService;
import com.github.velinyordanov.foodorder.validation.ValidationConstraints;

@RestController
@RequestMapping("restaurants")
public class RestaurantsController {
	private final RestaurantsService restaurantsService;

	public RestaurantsController(RestaurantsService restaurantsService) {
		this.restaurantsService = restaurantsService;
	}

	@GetMapping()
	public Collection<RestaurantDto> getAll() {
		return this.restaurantsService.getAll();
	}

	@GetMapping("{restaurantId}")
	public RestaurantDataDto getRestaurantData(@PathVariable String restaurantId) {
		return this.restaurantsService.getRestaurantData(restaurantId);
	}

	@PutMapping("{restaurantId}")
	@PreAuthorize(ValidationConstraints.ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
	public RestaurantDataDto editRestaurant(@PathVariable String restaurantId,
			@RequestBody @Valid RestaurantEditDto restaurantEditDto) {
		return this.restaurantsService.editRestaurant(restaurantId, restaurantEditDto);
	}
}
