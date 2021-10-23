package com.github.velinyordanov.foodorder.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.*;

public class UserLoginDto {
	@NotBlank(message = EMPTY_EMAIL)
	@Email(message = NOT_EMAIL)
	@Size(min = MIN_LENGTH_EMAIL, max = MAX_LENGTH_EMAIL, message = EMAIL_OUT_OF_BOUNDS)
	private String email;

	@NotBlank(message = EMPTY_PASSWORD)
	@Size(min = MIN_LENGTH_PASSWORD, max = MAX_LENGTH_PASSWORD, message = PASSWORD_OUT_OF_BOUNDS)
	@Pattern(regexp = PASSWORD_PATTERN_LOWERCASE, message = PASSWORD_PATTERN)
	@Pattern(regexp = PASSWORD_PATTERN_UPPERCASE, message = PASSWORD_PATTERN)
	@Pattern(regexp = PASSWORD_PATTERN_NUMBER, message = PASSWORD_PATTERN)
	private String password;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
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
		UserLoginDto other = (UserLoginDto) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		return true;
	}
}
