package com.github.velinyordanov.foodorder.dto;

import javax.validation.constraints.NotBlank;

public class CategoryDto {
    private String id;

    @NotBlank(message = "Category name is required!")
    private String name;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }
}
