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

    public RestaurantsCategoriesServiceImpl(
	    FoodOrderData foodOrderData,
	    Mapper mapper) {
	this.foodOrderData = foodOrderData;
	this.mapper = mapper;
    }

    @Override
    public void deleteCategory(String restaurantId, String categoryId) {
	this.foodOrderData.restaurants().findById(restaurantId).ifPresentOrElse(restaurant -> {
	    this.foodOrderData.categories()
		    .findByRestaurantId(restaurantId)
		    .stream()
		    .filter(category -> categoryId.equals(category.getId()))
		    .findFirst()
		    .ifPresentOrElse(category -> {
			if (category.getFoods().isEmpty()) {
			    this.foodOrderData.categories().delete(category);
			} else {
			    throw new NonEmptyCategoryException(MessageFormat
				    .format("Category {0} has foods associated with it!", category.getName()));
			}
		    }, () -> {
			throw new NotFoundException(
				MessageFormat.format("Cateogry with id {0} not found!", categoryId));
		    });
	}, () -> {
	    throw new NotFoundException(MessageFormat.format("Restaurant with id {0} not found!", restaurantId));
	});
    }

    @Override
    public Optional<CategoryDto> addCategoryForRestaurant(String restaurantId, CategoryCreateDto categoryCreateDto) {
	Optional<Restaurant> restaurantOptional = this.foodOrderData
		.restaurants()
		.findById(restaurantId);

	if (restaurantOptional.isEmpty()) {
	    throw new NotFoundException(MessageFormat.format("Restaurant with id {0} not found", restaurantId));
	}

	Optional<Category> categoryOptional =
		restaurantOptional
			.flatMap(restaurant -> this.foodOrderData
				.categories()
				.findByNameIncludingDeleted(categoryCreateDto.getName()));

	if (categoryOptional.isEmpty()) {
	    Category category = this.mapper.map(categoryCreateDto, Category.class);
	    category.setRestaurant(restaurantOptional.get());
	    return Optional.of(this.mapper.map(this.foodOrderData.categories().save(category), CategoryDto.class));
	} else {
	    Category category = categoryOptional.get();
	    if (category.getIsDeleted()) {
		category.setIsDeleted(false);
		return Optional.of(this.mapper.map(this.foodOrderData.categories().save(category), CategoryDto.class));
	    } else {
		throw new DuplicateCategoryException(MessageFormat
			.format("Category {0} already exists", categoryCreateDto.getName()));
	    }
	}
    }

    @Override
    public Collection<CategoryDto> getCategoriesForRestaurant(String restaurantId) {
	Optional<Restaurant> restaurantOptional =
		this.foodOrderData.restaurants()
			.findById(restaurantId);

	if (restaurantOptional.isEmpty()) {
	    throw new NotFoundException(MessageFormat.format("Restaurant with id {0} not found", restaurantId));
	}

	return restaurantOptional
		.map(restaurant -> this.foodOrderData.categories()
			.findByRestaurantId(restaurant.getId())
			.stream()
			.map(category -> this.mapper.map(category, CategoryDto.class))
			.collect(Collectors.toList()))
		.orElseThrow(() -> new IllegalStateException(
			"An error occurred while loading categories. Try again later."));
    }
}
