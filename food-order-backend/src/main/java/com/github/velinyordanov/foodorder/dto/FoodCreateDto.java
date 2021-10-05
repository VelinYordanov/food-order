package com.github.velinyordanov.foodorder.dto;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.*;

public class FoodCreateDto {
	@NotBlank(message = EMPTY_FOOD_NAME)
	private String name;

	@NotBlank(message = EMPTY_FOOD_DESCRIPTION)
	private String description;

	@NotNull(message = EMPTY_FOOD_PRICE)
	@Positive(message = ZERO_OR_NEGATIVE_FOOD_PRICE)
	private BigDecimal price;

	@NotEmpty(message = EMPTY_FOOD_CATEGORIES)
	private Set<CategoryDto> categories;

	public FoodCreateDto() {
		this.categories = new HashSet<>();
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

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Set<CategoryDto> getCategories() {
		return categories;
	}

	public void setCategories(Set<CategoryDto> categories) {
		this.categories = categories;
	}
}
