package com.github.velinyordanov.foodorder.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.github.velinyordanov.foodorder.validation.ValidationConstraints;

public class CategoryCreateDto {
	@NotBlank(message = ValidationConstraints.EMPTY_CATEGORY_NAME)
	@Size(min = ValidationConstraints.MIN_LENGTH_CATEGORY_NAME, max = ValidationConstraints.MAX_LENGTH_CATEGORY_NAME, message = ValidationConstraints.CATEGORY_NAME_OUT_OF_BOUNDS)
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
