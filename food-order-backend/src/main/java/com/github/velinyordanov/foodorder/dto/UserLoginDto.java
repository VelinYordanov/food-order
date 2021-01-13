package com.github.velinyordanov.foodorder.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.github.velinyordanov.foodorder.config.ValidationConstraints;

public class UserLoginDto {
    @NotEmpty(message = ValidationConstraints.EMPTY_USERNAME)
    @Email(message = ValidationConstraints.NOT_EMAIL)
    @Size(
	    min = ValidationConstraints.MIN_LENGTH_USERNAME,
	    max = ValidationConstraints.MAX_LENGTH_USERNAME,
	    message = ValidationConstraints.USERNAME_OUT_OF_BOUNDS)
    private String email;

    @NotEmpty(message = ValidationConstraints.EMPTY_PASSWORD)
    @Size(
	    min = ValidationConstraints.MIN_LENGTH_PASSWORD,
	    max = ValidationConstraints.MAX_LENGTH_PASSWORD,
	    message = ValidationConstraints.PASSWORD_OUT_OF_BOUNDS)
    @Pattern(regexp = ValidationConstraints.PASSWORD_PATTERN_LOWERCASE,
	    message = ValidationConstraints.PASSWORD_PATTERN)
    @Pattern(regexp = ValidationConstraints.PASSWORD_PATTERN_UPPERCASE,
	    message = ValidationConstraints.PASSWORD_PATTERN)
    @Pattern(regexp = ValidationConstraints.PASSWORD_PATTERN_NUMBER,
	    message = ValidationConstraints.PASSWORD_PATTERN)
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
