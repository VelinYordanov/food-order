package com.github.velinyordanov.foodorder.dto;

import javax.validation.constraints.NotBlank;

public class CategoryCreateDto {
	@NotBlank(message = "Category name is required")
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
