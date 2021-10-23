package com.github.velinyordanov.foodorder.services.restaurants.impl;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.github.velinyordanov.foodorder.data.FoodOrderData;
import com.github.velinyordanov.foodorder.data.entities.Category;
import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.dto.CategoryCreateDto;
import com.github.velinyordanov.foodorder.dto.CategoryDto;
import com.github.velinyordanov.foodorder.exceptions.DuplicateCategoryException;
import com.github.velinyordanov.foodorder.exceptions.NonEmptyCategoryException;
import com.github.velinyordanov.foodorder.exceptions.NotFoundException;
import com.github.velinyordanov.foodorder.mapping.Mapper;
import com.github.velinyordanov.foodorder.services.restaurants.RestaurantsCategoriesService;

@Service
public class RestaurantsCategoriesServiceImpl implements RestaurantsCategoriesService {
	private final FoodOrderData foodOrderData;
	private final Mapper mapper;

	public RestaurantsCategoriesServiceImpl(FoodOrderData foodOrderData, Mapper mapper) {
		this.foodOrderData = foodOrderData;
		this.mapper = mapper;
	}

	@Override
	public void deleteCategory(String restaurantId, String categoryId) {
		Category result = this.foodOrderData.categories()
				.findById(categoryId)
				.stream()
				.filter(category -> restaurantId.equals(category.getRestaurant().getId()))
				.peek(category -> {
					if (!category.getFoods().isEmpty()) {
						throw new NonEmptyCategoryException(MessageFormat
								.format("Category {0} has foods associated with it!", category.getName()));
					}
				})
				.findFirst()
				.orElseThrow(() -> new NotFoundException(
						MessageFormat.format("Cateogry with id {0} not found!", categoryId)));

		this.foodOrderData.categories().delete(result);
	}

	@Override
	public CategoryDto addCategoryForRestaurant(String restaurantId, CategoryCreateDto categoryCreateDto) {
		Restaurant restaurant = this.foodOrderData.restaurants()
				.findById(restaurantId)
				.orElseThrow(() -> new NotFoundException(MessageFormat.format("Restaurant with id {0} not found", restaurantId)));
				
		Optional<Category> existingCategoryOptional = this.foodOrderData.categories()
				.findByRestaurantAndNameIncludingDeleted(restaurantId, categoryCreateDto.getName());

		if (existingCategoryOptional.isPresent()) {
			Category existingCategory = existingCategoryOptional.get();
			if (!existingCategory.getIsDeleted()) {
				throw new DuplicateCategoryException(
						MessageFormat.format("Category {0} already exists", categoryCreateDto.getName()));
			}
			
			existingCategory.setIsDeleted(false);
			return this.mapper.map(this.foodOrderData.categories().save(existingCategory), CategoryDto.class);
		}
		
		Category category = this.mapper.map(categoryCreateDto, Category.class);
		category.setRestaurant(restaurant);
		return this.mapper.map(this.foodOrderData.categories().save(category), CategoryDto.class);
	}

	@Override
	public Collection<CategoryDto> getCategoriesForRestaurant(String restaurantId) {
		return this.foodOrderData.categories()
				.findByRestaurantId(restaurantId)
				.stream()
				.map(category -> this.mapper.map(category, CategoryDto.class))
				.collect(Collectors.toList());
	}
}
