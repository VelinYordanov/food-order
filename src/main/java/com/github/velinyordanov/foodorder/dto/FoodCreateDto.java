package com.github.velinyordanov.foodorder.dto;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

public class FoodCreateDto {
    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @Positive
    private BigDecimal price;
    private Set<CategoryCreateDto> categories;

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

    public Set<CategoryCreateDto> getCategories() {
	return categories;
    }

    public void setCategories(Set<CategoryCreateDto> categories) {
	this.categories = categories;
    }
}
