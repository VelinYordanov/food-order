package com.github.velinyordanov.foodorder.controllers.restaurants;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.velinyordanov.foodorder.dto.FoodCreateDto;
import com.github.velinyordanov.foodorder.dto.FoodDto;
import com.github.velinyordanov.foodorder.services.restaurants.RestaurantsFoodsService;
import com.github.velinyordanov.foodorder.validation.ValidationConstraints;

@RestController
@RequestMapping("restaurants/{restaurantId}/foods")
@PreAuthorize(ValidationConstraints.ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
public class RestaurantsFoodsController {
    private final RestaurantsFoodsService restaurantsFoodsService;

    public RestaurantsFoodsController(RestaurantsFoodsService restaurantsFoodsService) {
	this.restaurantsFoodsService = restaurantsFoodsService;
    }

    @PostMapping()
    public FoodDto addFoodToRestaurant(@PathVariable String restaurantId,
	    @RequestBody @Valid FoodCreateDto foodCreateDto) {
	return this.restaurantsFoodsService.addFoodToRestaurant(restaurantId, foodCreateDto);
    }

    @PutMapping("{foodId}")
    public FoodDto editFood(
	    @PathVariable String restaurantId,
	    @PathVariable String foodId,
	    @RequestBody @Valid FoodCreateDto foodCreateDto) {
	return this.restaurantsFoodsService.editFood(restaurantId, foodId, foodCreateDto);
    }

    @DeleteMapping("{foodId}")
    public void deleteFoodFromRestaurant(
	    @PathVariable String restaurantId,
	    @PathVariable String foodId) {
	this.restaurantsFoodsService.deleteFood(restaurantId, foodId);
    }
}
