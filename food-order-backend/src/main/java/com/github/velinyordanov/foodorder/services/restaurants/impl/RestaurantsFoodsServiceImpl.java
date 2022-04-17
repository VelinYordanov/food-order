package com.github.velinyordanov.foodorder.services.restaurants.impl;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.github.velinyordanov.foodorder.data.FoodOrderData;
import com.github.velinyordanov.foodorder.data.entities.Category;
import com.github.velinyordanov.foodorder.data.entities.Food;
import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.dto.FoodCreateDto;
import com.github.velinyordanov.foodorder.dto.FoodDto;
import com.github.velinyordanov.foodorder.exceptions.BadRequestException;
import com.github.velinyordanov.foodorder.exceptions.ForeignCategoryException;
import com.github.velinyordanov.foodorder.exceptions.NotFoundException;
import com.github.velinyordanov.foodorder.exceptions.UnrecognizedCategoriesException;
import com.github.velinyordanov.foodorder.mapping.Mapper;
import com.github.velinyordanov.foodorder.services.restaurants.RestaurantsFoodsService;

@Service
@Transactional
public class RestaurantsFoodsServiceImpl implements RestaurantsFoodsService {
	private final FoodOrderData foodOrderData;
	private final Mapper mapper;

	public RestaurantsFoodsServiceImpl(FoodOrderData foodOrderData, Mapper mapper) {
		this.foodOrderData = foodOrderData;
		this.mapper = mapper;
	}

	@Override
	public FoodDto addFoodToRestaurant(String restaurantId, FoodCreateDto foodCreateDto) {
		Restaurant restaurant = this.foodOrderData.restaurants()
				.findById(restaurantId)
				.orElseThrow(() -> new NotFoundException(
						MessageFormat.format("Restaurant with id {0} not found!", restaurantId)));

		Food food = this.mapper.map(foodCreateDto, Food.class);

		Collection<String> categoryIds = food.getCategories()
				.stream()
				.map(x -> x.getId())
				.collect(Collectors.toList());

		Collection<Category> existingCategories = this.foodOrderData.categories().findAllById(categoryIds);

		if (categoryIds.size() != existingCategories.size()) {
			String unrecognizedCategories = food.getCategoriesWithDeleted()
					.stream()
					.filter(category -> !existingCategories.contains(category))
					.map(category -> category.getName())
					.collect(Collectors.joining(", "));

			throw new UnrecognizedCategoriesException(MessageFormat.format(
					"Unrecognized categories detected. You need to create them first. {0}", unrecognizedCategories));
		}

		existingCategories.forEach(category -> {
			if (!restaurant.getId().equals(category.getRestaurant().getId())) {
				throw new ForeignCategoryException("Category belongs to another restaurant");
			}

			food.removeCategory(category);
			category.addFood(food);
		});

		return this.mapper.map(this.foodOrderData.foods().save(food), FoodDto.class);
	}

	@Override
	public FoodDto editFood(String restaurantId, String foodId, FoodCreateDto foodCreateDto) {
		Food food = this.foodOrderData.foods()
				.findById(foodId)
				.orElseThrow(() -> new NotFoundException(MessageFormat.format("Food with id {0} not found", foodId)));

		food.setName(foodCreateDto.getName());
		food.setPrice(foodCreateDto.getPrice());
		food.setDescription(foodCreateDto.getDescription());

		Collection<Category> categories = this.foodOrderData.categories().findByRestaurantId(restaurantId);
		Collection<String> selectedCategoryIds = foodCreateDto.getCategories()
				.stream()
				.map(x -> x.getId())
				.collect(Collectors.toList());

		categories.forEach(category -> {
			if (selectedCategoryIds.contains(category.getId())) {
				category.addFood(food);
			} else {
				category.removeFood(food);
			}
		});

		if (food.getCategories().isEmpty()) {
			throw new BadRequestException("At least one valid category is required.");
		}

		return this.mapper.map(this.foodOrderData.foods().save(food), FoodDto.class);
	}

	@Override
	public void deleteFood(String restaurantId, String foodId) {
		this.foodOrderData.restaurants()
				.findById(restaurantId)
				.orElseThrow(() -> new NotFoundException(
						MessageFormat.format("Restaurant with id {0} not found", restaurantId)));

		Food food = this.foodOrderData.foods()
				.findById(foodId)
				.orElseThrow(() -> new NotFoundException(
						MessageFormat.format("Could not find food with id {0}", foodId)));

		if (!food.getCategories()
				.stream()
				.allMatch(category -> restaurantId.equals(category.getRestaurant().getId()))) {
			throw new NotFoundException("Could not find food for restaurant");
		}

		this.foodOrderData.foods().delete(food);
	}
}
