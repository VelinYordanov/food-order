package com.github.velinyordanov.foodorder.services.restaurants;

import java.util.Collection;

import com.github.velinyordanov.foodorder.dto.CategoryCreateDto;
import com.github.velinyordanov.foodorder.dto.CategoryDto;

public interface RestaurantsCategoriesService {
	CategoryDto addCategoryForRestaurant(String restaurantId, CategoryCreateDto categoryCreateDto);

	Collection<CategoryDto> getCategoriesForRestaurant(String restaurantId);

	void deleteCategory(String restaurantId, String categoryId);
}
