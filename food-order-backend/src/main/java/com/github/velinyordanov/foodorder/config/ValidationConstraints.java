package com.github.velinyordanov.foodorder.config;

public class ValidationConstraints {
    public static final int MIN_LENGTH_USERNAME = 5;
    public static final int MAX_LENGTH_USERNAME = 50;
    public static final String EMPTY_USERNAME = "Username is required";
    public static final String USERNAME_OUT_OF_BOUNDS =
	    "Username must be between " + MIN_LENGTH_USERNAME + " and " + MAX_LENGTH_USERNAME + " symbols.";

    public static final int MIN_LENGTH_PASSWORD = 5;
    public static final int MAX_LENGTH_PASSWORD = 50;
    public static final String EMPTY_PASSWORD = "Password is required";
    public static final String PASSWORD_OUT_OF_BOUNDS =
	    "Password must be between " + MIN_LENGTH_PASSWORD + " and " + MAX_LENGTH_PASSWORD + " symbols.";

    public static final int MIN_LENGTH_NAME = 5;
    public static final int MAX_LENGTH_NAME = 50;
    public static final String EMPTY_NAME = "Name is required";
    public static final String NAME_OUT_OF_BOUNDS =
	    "Name must be between " + MIN_LENGTH_USERNAME + " and " + MAX_LENGTH_USERNAME + " symbols.";

    private ValidationConstraints() {
    }
}
