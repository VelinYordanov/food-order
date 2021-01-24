package com.github.velinyordanov.foodorder.services.restaurants.impl;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.github.velinyordanov.foodorder.data.FoodOrderData;
import com.github.velinyordanov.foodorder.data.entities.Category;
import com.github.velinyordanov.foodorder.data.entities.Food;
import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.dto.FoodCreateDto;
import com.github.velinyordanov.foodorder.dto.FoodDto;
import com.github.velinyordanov.foodorder.exceptions.DuplicateCategoryException;
import com.github.velinyordanov.foodorder.exceptions.NotFoundException;
import com.github.velinyordanov.foodorder.mapping.Mapper;
import com.github.velinyordanov.foodorder.services.restaurants.RestaurantsFoodsService;

@Service
public class RestaurantsFoodsServiceImpl implements RestaurantsFoodsService {
    private final FoodOrderData foodOrderData;
    private final Mapper mapper;

    public RestaurantsFoodsServiceImpl(
	    FoodOrderData foodOrderData,
	    Mapper mapper) {
	this.foodOrderData = foodOrderData;
	this.mapper = mapper;
    }

    @Override
    public FoodDto addFoodToRestaurant(String restaurantId, FoodCreateDto foodCreateDto) {
	Optional<Restaurant> restaurantOptional = this.foodOrderData.restaurants().findById(restaurantId);
	if (restaurantOptional.isEmpty()) {
	    throw new NotFoundException(MessageFormat.format("Restaurant with id {0} not found!", restaurantId));
	}

	Restaurant restaurant = restaurantOptional.get();

	Food food = this.mapper.map(foodCreateDto, Food.class);

	Collection<String> categoryIds =
		food.getCategories()
			.stream()
			.map(x -> x.getId())
			.collect(Collectors.toList());

	Set<Category> existingCategories = new HashSet<>();
	this.foodOrderData.categories()
		.findAllById(categoryIds)
		.forEach(existingCategories::add);

	existingCategories.forEach(category -> {
	    if (!restaurant.getId().equals(category.getRestaurant().getId())) {
		throw new RuntimeException("Category belongs to another restaurant");
	    }

	    food.removeCategory(category);
	    category.addFood(food);
	});

	Collection<Category> newCategories = food.getCategoriesWithDeleted()
		.stream()
		.filter(category -> !existingCategories.contains(category))
		.collect(Collectors.toList());

	newCategories.forEach(newCategory -> {
	    restaurant.getCategoriesWithDeleted()
		    .stream()
		    .filter(category -> category.getName().equals(newCategory.getName()))
		    .findFirst()
		    .ifPresentOrElse(category -> {
			if (category.getIsDeleted()) {
			    category.setIsDeleted(false);
			    food.removeCategory(newCategory);
			    category.addFood(food);
			} else {
			    throw new DuplicateCategoryException(
				    MessageFormat.format(
					    "Category with name: {0} already exists for this restaurant.",
					    newCategory.getName()));
			}
		    }, () -> {
			restaurant.addCategory(newCategory);
			newCategory.addFood(food);
		    });
	});

	return this.mapper.map(this.foodOrderData.foods().save(food), FoodDto.class);
    }

    @Override
    public FoodDto editFood(String restaurantId, String foodId, FoodCreateDto foodCreateDto) {
	Optional<Food> foodOptional = this.foodOrderData.foods().findById(foodId);
	if (foodOptional.isEmpty()) {
	    throw new NotFoundException(MessageFormat.format("Food with id {0} not found", foodId));
	}

	Food food = foodOptional.get();

	food.setName(foodCreateDto.getName());
	food.setPrice(foodCreateDto.getPrice());
	food.setDescription(foodCreateDto.getDescription());

	Collection<Category> categories = this.foodOrderData.categories().findByRestaurantId(restaurantId);
	Collection<String> selectedCategoryIds =
		foodCreateDto.getCategories().stream().map(x -> x.getId()).collect(Collectors.toList());
	categories.forEach(category -> {
	    if (selectedCategoryIds.contains(category.getId())) {
		category.addFood(food);
	    } else {
		category.removeFood(food);
	    }
	});

	return this.mapper.map(this.foodOrderData.foods().save(food), FoodDto.class);
    }

    @Override
    public void deleteFood(String restaurantId, String foodId) {
	this.foodOrderData.restaurants().findById(restaurantId).ifPresentOrElse(restaurant -> {
	    this.foodOrderData.foods()
		    .findById(foodId)
		    .ifPresentOrElse(food -> {
			if (food.getCategories()
				.stream()
				.allMatch(category -> restaurantId.equals(category.getRestaurant().getId()))) {
			    this.foodOrderData.foods().delete(food);
			} else {
			    throw new NotFoundException("Could not find food for restaurant");
			}
		    }, () -> {
			throw new NotFoundException(
				MessageFormat.format("Could not find food with id {0}", foodId));
		    });
	}, () -> {
	    throw new NotFoundException(MessageFormat.format("Restaurant with id {0} not found", restaurantId));
	});
    }
}
