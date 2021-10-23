package com.github.velinyordanov.foodorder.controllers.restaurants;

import java.util.Collection;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.velinyordanov.foodorder.dto.CategoryCreateDto;
import com.github.velinyordanov.foodorder.dto.CategoryDto;
import com.github.velinyordanov.foodorder.services.restaurants.RestaurantsCategoriesService;
import com.github.velinyordanov.foodorder.validation.ValidationConstraints;

@RestController
@RequestMapping("restaurants/{restaurantId}/categories")
@PreAuthorize(ValidationConstraints.ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION)
public class RestaurantsCategoriesController {
	private final RestaurantsCategoriesService restaurantsCategoriesService;

	public RestaurantsCategoriesController(RestaurantsCategoriesService restaurantsCategoriesService) {
		this.restaurantsCategoriesService = restaurantsCategoriesService;
	}

	@GetMapping
	public Collection<CategoryDto> getCategories(@PathVariable String restaurantId) {
		return this.restaurantsCategoriesService.getCategoriesForRestaurant(restaurantId);
	}

	@DeleteMapping("{categoryId}")
	public void deleteCategoryFromRestaurant(@PathVariable String restaurantId, @PathVariable String categoryId) {
		this.restaurantsCategoriesService.deleteCategory(restaurantId, categoryId);
	}

	@PostMapping()
	public CategoryDto addCategoryToRestaurant(@PathVariable String restaurantId,
			@RequestBody @Valid CategoryCreateDto categoryCreateDto) {
		return this.restaurantsCategoriesService.addCategoryForRestaurant(restaurantId, categoryCreateDto);
	}
}
