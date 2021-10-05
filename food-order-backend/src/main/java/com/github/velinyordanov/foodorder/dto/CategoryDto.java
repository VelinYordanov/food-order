package com.github.velinyordanov.foodorder.dto;

import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.*;

public class CategoryDto {
	private String id;

	@NotBlank(message = EMPTY_CATEGORY_NAME)
	@Size(min = MIN_LENGTH_CATEGORY_NAME, max = MAX_LENGTH_CATEGORY_NAME, message = CATEGORY_NAME_OUT_OF_BOUNDS)
	private String name;
	
	public CategoryDto() {
		this.setId(UUID.randomUUID().toString());
	}

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
