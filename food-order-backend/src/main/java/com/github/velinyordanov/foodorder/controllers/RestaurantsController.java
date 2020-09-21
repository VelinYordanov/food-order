package com.github.velinyordanov.foodorder.controllers;

import java.util.Collection;

import javax.validation.Valid;

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

import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.dto.CategoryCreateDto;
import com.github.velinyordanov.foodorder.dto.CategoryDto;
import com.github.velinyordanov.foodorder.dto.DiscountCodeCreateDto;
import com.github.velinyordanov.foodorder.dto.DiscountCodeDto;
import com.github.velinyordanov.foodorder.dto.FoodCreateDto;
import com.github.velinyordanov.foodorder.dto.FoodDto;
import com.github.velinyordanov.foodorder.dto.JwtTokenDto;
import com.github.velinyordanov.foodorder.dto.OrderDto;
import com.github.velinyordanov.foodorder.dto.OrderListDto;
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
    public Collection<RestaurantDto> getAll() {
	return this.restaurantsService.getAll();
    }

    @GetMapping("{restaurantId}")
    public RestaurantDataDto getRestaurantData(@PathVariable String restaurantId) {
	return this.restaurantsService.getRestaurantData(restaurantId);
    }

    @PostMapping()
    public JwtTokenDto register(@Valid @RequestBody RestaurantRegisterDto userDto) {
	return new JwtTokenDto(restaurantsService.register(userDto));
    }

    @PostMapping("tokens")
    public JwtTokenDto login(@Valid @RequestBody UserDto user) {
	return new JwtTokenDto(restaurantsService.login(user));
    }

    @PostMapping("{restaurantId}/foods")
    @PreAuthorize(ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
    public FoodDto addFoodToRestaurant(@PathVariable String restaurantId,
	    @RequestBody @Valid FoodCreateDto foodCreateDto) {
	return this.restaurantsService.addFoodToRestaurant(restaurantId, foodCreateDto);
    }

    @PutMapping("{restaurantId}/foods/{foodId}")
    @PreAuthorize(ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
    public FoodDto editFood(
	    @PathVariable String restaurantId,
	    @PathVariable String foodId,
	    @RequestBody @Valid FoodCreateDto foodCreateDto) {
	return this.restaurantsService.editFood(restaurantId, foodId, foodCreateDto);
    }

    @GetMapping("{restaurantId}/categories")
    @PreAuthorize(ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
    public Collection<CategoryDto> getCategories(@PathVariable String restaurantId) {
	return this.restaurantsService.getCategoriesForRestaurant(restaurantId);
    }

    @PutMapping("{restaurantId}")
    @PreAuthorize(ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
    public RestaurantDataDto editRestaurant(
	    @PathVariable String restaurantId,
	    @RequestBody @Valid RestaurantEditDto restaurantEditDto) {
	return this.restaurantsService.editRestaurant(restaurantId, restaurantEditDto);
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

    @GetMapping("{restaurantId}/orders")
    @PreAuthorize(ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
    public Collection<OrderListDto> getRestaurantOrders(@PathVariable String restaurantId) {
	return this.restaurantsService.getRestaurantOrders(restaurantId);
    }

    @GetMapping("{restaurantId}/orders/{orderId}")
    @PreAuthorize(ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
    public OrderDto getRestaurantOrder(@PathVariable String restaurantId, @PathVariable String orderId) {
	return this.restaurantsService.getRestaurantOrder(restaurantId, orderId);
    }

    @PostMapping("{restaurantId}/discount-codes")
    @PreAuthorize(ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
    public DiscountCodeDto addDiscountCodeToRestaurant(@PathVariable String restaurantId,
	    @Valid @RequestBody DiscountCodeCreateDto discountCode) {
	return this.restaurantsService.addDiscountCodeToRestaurant(restaurantId, discountCode);
    }

    @GetMapping("{restaurantId}/discount-codes/{code}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public DiscountCodeDto getDiscountCode(
	    @PathVariable String restaurantId,
	    @PathVariable String code,
	    @AuthenticationPrincipal Customer customer) {
	return this.restaurantsService.getDiscountByCode(restaurantId, code, customer.getId());
    }
}
