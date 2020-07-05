package com.github.velinyordanov.foodorder.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.github.velinyordanov.foodorder.config.ValidationConstraints;

public class UserDto {
    @NotEmpty(message = ValidationConstraints.EMPTY_USERNAME)
    @Size(
	    min = ValidationConstraints.MIN_LENGTH_USERNAME,
	    max = ValidationConstraints.MAX_LENGTH_USERNAME,
	    message = ValidationConstraints.USERNAME_OUT_OF_BOUNDS)
    private String username;

    @NotEmpty(message = ValidationConstraints.EMPTY_PASSWORD)
    @Size(
	    min = ValidationConstraints.MIN_LENGTH_PASSWORD,
	    max = ValidationConstraints.MAX_LENGTH_PASSWORD,
	    message = ValidationConstraints.PASSWORD_OUT_OF_BOUNDS)
    private String password;

    public String getUsername() {
	return username;
    }

    public void setUsername(String userName) {
	this.username = userName;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }
}
