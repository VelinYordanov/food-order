package com.github.velinyordanov.foodorder.dto;

import javax.validation.constraints.NotBlank;

import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.EMPTY_NAME;

public class RestaurantEditDto {
	@NotBlank(message = EMPTY_NAME)
	private String name;

	private String description;

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
}
