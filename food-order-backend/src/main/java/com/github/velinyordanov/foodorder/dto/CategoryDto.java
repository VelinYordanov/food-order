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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		CategoryDto other = (CategoryDto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
