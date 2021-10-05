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
}
