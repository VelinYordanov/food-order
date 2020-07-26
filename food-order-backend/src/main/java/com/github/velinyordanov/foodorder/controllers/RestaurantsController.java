package com.github.velinyordanov.foodorder.controllers;

import java.util.Collection;

import javax.validation.Valid;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.dto.CategoryCreateDto;
import com.github.velinyordanov.foodorder.dto.CategoryDto;
import com.github.velinyordanov.foodorder.dto.FoodCreateDto;
import com.github.velinyordanov.foodorder.dto.JwtTokenDto;
import com.github.velinyordanov.foodorder.dto.RestaurantDataDto;
import com.github.velinyordanov.foodorder.dto.RestaurantDto;
import com.github.velinyordanov.foodorder.dto.RestaurantEditDto;
import com.github.velinyordanov.foodorder.dto.RestaurantRegisterDto;
import com.github.velinyordanov.foodorder.dto.UserDto;
import com.github.velinyordanov.foodorder.services.JwtTokenService;
import com.github.velinyordanov.foodorder.services.RestaurantsService;

@RestController
@RequestMapping("restaurants")
public class RestaurantsController {
    private static final String ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION =
	    "hasAuthority('ROLE_RESTAURANT') and principal.id == #restaurantId";

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
    public JwtTokenDto register(@Valid @RequestBody RestaurantRegisterDto userDto) {
	return new JwtTokenDto(restaurantsService.register(userDto));
    }

    @PostMapping("tokens")
    public JwtTokenDto login(@Valid @RequestBody UserDto user) {
	return new JwtTokenDto(restaurantsService.login(user));
    }

    @PostMapping("foods")
    @Secured("ROLE_RESTAURANT")
    public void addFoodToRestaurant(@AuthenticationPrincipal Restaurant restaurant,
	    @RequestBody @Valid FoodCreateDto foodCreateDto) {
	this.restaurantsService.addFoodsToRestaurant(restaurant.getId(), foodCreateDto);
    }

    @PutMapping("{restaurantId}/foods/{foodId}")
    @PreAuthorize(ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
    public void editFood(
	    @PathVariable String restaurantId,
	    @PathVariable String foodId,
	    @RequestBody @Valid FoodCreateDto foodCreateDto) {
	this.restaurantsService.editFood(restaurantId, foodId, foodCreateDto);
    }

    @GetMapping("{restaurantId}")
    @PreAuthorize(ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
    public RestaurantDataDto getRestaurantData(@PathVariable String restaurantId) {
	return this.restaurantsService.getRestaurantData(restaurantId);
    }

    @GetMapping("{restaurantId}/categories")
    @PreAuthorize(ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
    public Collection<CategoryDto> getCategories(@PathVariable String restaurantId) {
	return this.restaurantsService.getCategoriesForRestaurant(restaurantId);
    }

    @PutMapping("{restaurantId}")
    @PreAuthorize(ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
    public void editRestaurant(
	    @PathVariable String restaurantId,
	    @RequestBody @Valid RestaurantEditDto restaurantEditDto) {
	this.restaurantsService.editRestaurant(restaurantId, restaurantEditDto);
    }

    @DeleteMapping("{restaurantId}/categories/{categoryId}")
    @PreAuthorize(ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
    public void deleteCategoryFromRestaurant(
	    @PathVariable String restaurantId,
	    @PathVariable String categoryId) {
	this.restaurantsService.deleteCategory(restaurantId, categoryId);
    }

    @DeleteMapping("{restaurantId}/foods/{foodId}")
    @PreAuthorize(ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
    public void deleteFoodFromRestaurant(
	    @PathVariable String restaurantId,
	    @PathVariable String foodId) {
	this.restaurantsService.deleteFood(restaurantId, foodId);
    }

    @PostMapping("{restaurantId}/categories")
    @PreAuthorize(ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
    public CategoryDto addCategoryToRestaurant(
	    @PathVariable String restaurantId,
	    @RequestBody @Valid CategoryCreateDto categoryCreateDto) {
	return this.restaurantsService.addCategoryForRestaurant(restaurantId, categoryCreateDto)
		.orElseThrow(() -> new IllegalStateException("An error occurred while creating category"));
    }
}
