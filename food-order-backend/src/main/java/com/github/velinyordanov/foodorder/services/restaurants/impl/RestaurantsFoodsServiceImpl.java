package com.github.velinyordanov.foodorder.services.restaurants.impl;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.github.velinyordanov.foodorder.data.FoodOrderData;
import com.github.velinyordanov.foodorder.data.entities.Category;
import com.github.velinyordanov.foodorder.data.entities.Food;
import com.github.velinyordanov.foodorder.data.entities.Restaurant;
import com.github.velinyordanov.foodorder.dto.FoodCreateDto;
import com.github.velinyordanov.foodorder.dto.FoodDto;
import com.github.velinyordanov.foodorder.exceptions.DuplicateCategoryException;
import com.github.velinyordanov.foodorder.exceptions.ForeignCategoryException;
import com.github.velinyordanov.foodorder.exceptions.NotFoundException;
import com.github.velinyordanov.foodorder.exceptions.UnrecognizedCategoriesException;
import com.github.velinyordanov.foodorder.mapping.Mapper;
import com.github.velinyordanov.foodorder.services.restaurants.RestaurantsFoodsService;

@Service
public class RestaurantsFoodsServiceImpl implements RestaurantsFoodsService {
	private final FoodOrderData foodOrderData;
	private final Mapper mapper;

	public RestaurantsFoodsServiceImpl(FoodOrderData foodOrderData, Mapper mapper) {
		this.foodOrderData = foodOrderData;
		this.mapper = mapper;
	}

	@Override
	@Transactional
	public FoodDto addFoodToRestaurant(String restaurantId, FoodCreateDto foodCreateDto) {
		Restaurant restaurant = this.foodOrderData.restaurants()
				.findById(restaurantId)
				.orElseThrow(() -> new NotFoundException(MessageFormat.format("Restaurant with id {0} not found!", restaurantId)));

		Food food = this.mapper.map(foodCreateDto, Food.class);

		Collection<String> categoryIds = food.getCategories()
				.stream()
				.map(x -> x.getId())
				.collect(Collectors.toList());

		Set<Category> existingCategories = new HashSet<>();
		this.foodOrderData.categories().findAllById(categoryIds).forEach(existingCategories::add);

		existingCategories.forEach(category -> {
			if (!restaurant.getId().equals(category.getRestaurant().getId())) {
				throw new ForeignCategoryException("Category belongs to another restaurant");
			}

			food.removeCategory(category);
			category.addFood(food);
		});

		String unrecognizedCategories = food.getCategoriesWithDeleted()
				.stream()
				.filter(category -> !existingCategories.contains(category))
				.map(category -> category.getName())
				.collect(Collectors.joining(", "));

		if(!unrecognizedCategories.isEmpty()) {
			throw new UnrecognizedCategoriesException(MessageFormat.format("Unrecognized categories detected. You need to create them first. {0}", unrecognizedCategories));
		}

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
		Collection<String> selectedCategoryIds = foodCreateDto.getCategories().stream().map(x -> x.getId())
				.collect(Collectors.toList());
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
			this.foodOrderData.foods().findById(foodId).ifPresentOrElse(food -> {
				if (food.getCategories().stream()
						.allMatch(category -> restaurantId.equals(category.getRestaurant().getId()))) {
					this.foodOrderData.foods().delete(food);
				} else {
					throw new NotFoundException("Could not find food for restaurant");
				}
			}, () -> {
				throw new NotFoundException(MessageFormat.format("Could not find food with id {0}", foodId));
			});
		}, () -> {
			throw new NotFoundException(MessageFormat.format("Restaurant with id {0} not found", restaurantId));
		});
	}
}
