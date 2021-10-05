package com.github.velinyordanov.foodorder.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.github.velinyordanov.foodorder.validation.annotations.NotDisposableEmail;

import static com.github.velinyordanov.foodorder.validation.ValidationConstraints.*;

public class CustomerRegisterDto {
	@NotBlank(message = EMPTY_EMAIL)
	@Email(message = NOT_EMAIL)
	@Size(min = MIN_LENGTH_EMAIL, max = MAX_LENGTH_EMAIL, message = EMAIL_OUT_OF_BOUNDS)
	@NotDisposableEmail
	private String email;

	@NotBlank(message = EMPTY_PASSWORD)
	@Pattern(regexp = PASSWORD_PATTERN_LOWERCASE, message = PASSWORD_PATTERN)
	@Pattern(regexp = PASSWORD_PATTERN_UPPERCASE, message = PASSWORD_PATTERN)
	@Pattern(regexp = PASSWORD_PATTERN_NUMBER, message = PASSWORD_PATTERN)
	@Size(min = MIN_LENGTH_PASSWORD, max = MAX_LENGTH_PASSWORD, message = PASSWORD_OUT_OF_BOUNDS)
	private String password;

	@NotBlank(message = EMPTY_NAME)
	@Pattern(regexp = NAME_PATTERN, message = NAME_DOES_NOT_MATCH_PATTERN)
	@Size(min = MIN_LENGTH_NAME, max = MAX_LENGTH_NAME, message = NAME_OUT_OF_BOUNDS)
	private String name;

	@NotEmpty(message = EMPTY_PHONE)
	private String phoneNumber;

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

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
}
