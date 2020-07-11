package com.github.velinyordanov.foodorder.dto;

import java.util.Collection;

public class FoodDto {
    private String id;
    private String name;
    private String description;
    private double Price;
    private Collection<CategoryDto> categories;

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

    public double getPrice() {
	return Price;
    }

    public void setPrice(double price) {
	Price = price;
    }

    public Collection<CategoryDto> getCategories() {
	return categories;
    }

    public void setCategories(Collection<CategoryDto> categories) {
	this.categories = categories;
    }
}
