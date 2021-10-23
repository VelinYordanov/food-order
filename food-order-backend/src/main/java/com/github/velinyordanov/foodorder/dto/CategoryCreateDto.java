package com.github.velinyordanov.foodorder.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.*;

public class CategoryCreateDto {
	@NotBlank(message = EMPTY_CATEGORY_NAME)
	@Size(min = MIN_LENGTH_CATEGORY_NAME, max = MAX_LENGTH_CATEGORY_NAME, message = CATEGORY_NAME_OUT_OF_BOUNDS)
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CategoryCreateDto other = (CategoryCreateDto) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
