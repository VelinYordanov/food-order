package com.github.velinyordanov.foodorder.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.github.velinyordanov.foodorder.config.ValidationConstraints;

public class RestaurantRegisterDto {
    @NotBlank(message = ValidationConstraints.EMPTY_USERNAME)
    @Email(message = ValidationConstraints.NOT_EMAIL)
    @Size(
	    min = ValidationConstraints.MIN_LENGTH_USERNAME,
	    max = ValidationConstraints.MAX_LENGTH_USERNAME,
	    message = ValidationConstraints.USERNAME_OUT_OF_BOUNDS)
    private String email;

    @NotBlank(message = ValidationConstraints.EMPTY_PASSWORD)
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

    @NotBlank(message = ValidationConstraints.EMPTY_NAME)
    @Pattern(regexp = ValidationConstraints.NAME_PATTERN, message = ValidationConstraints.NAME_DOES_NOT_MATCH_PATTERN)
    @Size(
	    min = ValidationConstraints.MIN_LENGTH_NAME,
	    max = ValidationConstraints.MAX_LENGTH_NAME,
	    message = ValidationConstraints.NAME_OUT_OF_BOUNDS)
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
}
