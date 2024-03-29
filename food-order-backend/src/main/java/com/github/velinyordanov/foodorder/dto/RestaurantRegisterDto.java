package com.github.velinyordanov.foodorder.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.github.velinyordanov.foodorder.validation.annotations.NotDisposableEmail;

import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.*;

public class RestaurantRegisterDto {
	@NotBlank(message = EMPTY_EMAIL)
	@Email(message = NOT_EMAIL)
	@Size(min = MIN_LENGTH_EMAIL, max = MAX_LENGTH_EMAIL, message = EMAIL_OUT_OF_BOUNDS)
	@NotDisposableEmail
	private String email;

	@NotBlank(message = EMPTY_PASSWORD)
	@Size(min = MIN_LENGTH_PASSWORD, max = MAX_LENGTH_PASSWORD, message = PASSWORD_OUT_OF_BOUNDS)
	@Pattern(regexp = PASSWORD_PATTERN_LOWERCASE, message = PASSWORD_PATTERN)
	@Pattern(regexp = PASSWORD_PATTERN_UPPERCASE, message = PASSWORD_PATTERN)
	@Pattern(regexp = PASSWORD_PATTERN_NUMBER, message = PASSWORD_PATTERN)
	private String password;

	@NotBlank(message = EMPTY_NAME)
	@Pattern(regexp = NAME_PATTERN, message = NAME_DOES_NOT_MATCH_PATTERN)
	@Size(min = MIN_LENGTH_NAME, max = MAX_LENGTH_NAME, message = NAME_OUT_OF_BOUNDS)
	private String name;

	private String description;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
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
		RestaurantRegisterDto other = (RestaurantRegisterDto) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		return true;
	}
}
