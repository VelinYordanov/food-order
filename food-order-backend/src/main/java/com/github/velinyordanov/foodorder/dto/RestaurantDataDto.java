package com.github.velinyordanov.foodorder.dto;

import java.util.Collection;

public class RestaurantDataDto {
	private String id;
	private String name;
	private String description;
	private Collection<CategoryDto> categories;
	private Collection<FoodDto> foods;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<CategoryDto> getCategories() {
		return categories;
	}

	public void setCategories(Collection<CategoryDto> categories) {
		this.categories = categories;
	}

	public Collection<FoodDto> getFoods() {
		return foods;
	}

	public void setFoods(Collection<FoodDto> foods) {
		this.foods = foods;
	}

}
