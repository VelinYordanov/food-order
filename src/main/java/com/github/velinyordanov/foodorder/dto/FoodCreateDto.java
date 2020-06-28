package com.github.velinyordanov.foodorder.dto;

import java.math.BigDecimal;
import java.util.Set;

public class FoodCreateDto {
    private String name;
    private String description;
    private BigDecimal price;
    private Set<CategoryCreateDto> categories;

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
