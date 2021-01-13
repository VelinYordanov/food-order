package com.github.velinyordanov.foodorder.dto;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class FoodCreateDto {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be above 0")
    private BigDecimal price;

    @NotEmpty(message = "At least one cateogry must be present")
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
