package com.github.velinyordanov.foodorder.validation;

public class ValidationConstraints {
	public static final int MIN_LENGTH_EMAIL = 5;
	public static final int MAX_LENGTH_EMAIL = 100;
	public static final String EMPTY_EMAIL = "Email is required";
	public static final String EMAIL_OUT_OF_BOUNDS = "Email must be between " + MIN_LENGTH_EMAIL + " and "
			+ MAX_LENGTH_EMAIL + " symbols.";

	public static final int MIN_LENGTH_PASSWORD = 5;
	public static final int MAX_LENGTH_PASSWORD = 50;
	public static final String EMPTY_PASSWORD = "Password is required";
	public static final String PASSWORD_OUT_OF_BOUNDS = "Password must be between " + MIN_LENGTH_PASSWORD + " and "
			+ MAX_LENGTH_PASSWORD + " symbols.";
	public static final String PASSWORD_PATTERN_LOWERCASE = ".*[a-z].*";
	public static final String PASSWORD_PATTERN_UPPERCASE = ".*[A-Z].*";
	public static final String PASSWORD_PATTERN_NUMBER = ".*[0-9].*";
	public static final String PASSWORD_PATTERN = "The password must contain a lowercase letter, uppercase letter and a number";

	public static final int MIN_LENGTH_NAME = 3;
	public static final int MAX_LENGTH_NAME = 100;
	public static final String EMPTY_NAME = "Name is required";
	public static final String NAME_OUT_OF_BOUNDS = "Name must be between " + MIN_LENGTH_EMAIL + " and "
			+ MAX_LENGTH_EMAIL + " symbols.";

	public static final String NOT_EMAIL = "A valid email must be provided";

	public static final String EMPTY_PHONE = "Phone number is required";

	public static final String NAME_PATTERN = "^(\\p{L}| )+$";
	public static final String NAME_DOES_NOT_MATCH_PATTERN = "Name can only contain alphabetic characters";

	public static final int MIN_LENGTH_FOOD_NAME = 3;
	public static final int MAX_LENGTH_FOOD_NAME = 100;
	public static final String EMPTY_FOOD_NAME = "Name is required";
	public static final String FOOD_NAME_OUT_OF_BOUNDS = "Name must be between " + MIN_LENGTH_FOOD_NAME + " and "
			+ MAX_LENGTH_FOOD_NAME + " symbols.";

	public static final int MIN_LENGTH_CATEGORY_NAME = 3;
	public static final int MAX_LENGTH_CATEGORY_NAME = 100;
	public static final String EMPTY_CATEGORY_NAME = "Name is required";
	public static final String CATEGORY_NAME_OUT_OF_BOUNDS = "Name must be between " + MIN_LENGTH_CATEGORY_NAME
			+ " and " + MAX_LENGTH_CATEGORY_NAME + " symbols.";

	public static final String EMPTY_CITY = "City is required";

	public static final String EMPTY_ADDRESS_TYPE = "Address type is required";

	public static final int MIN_LENGTH_NEIGHBORHOOD = 3;
	public static final int MAX_LENGTH_NEIGHBORHOOD = 100;
	public static final String EMPTY_NEIGHBORHOOD = "Neighborhood is required";
	public static final String NEIGHBORHOOD_OUT_OF_BOUNDS = "Neighborhood must be between " + MIN_LENGTH_NEIGHBORHOOD
			+ " and " + MAX_LENGTH_NEIGHBORHOOD + " symbols.";

	public static final int MIN_LENGTH_STREET = 3;
	public static final int MAX_LENGTH_STREET = 100;
	public static final String EMPTY_STREET = "Street is required";
	public static final String STREET_OUT_OF_BOUNDS = "Street must be between " + MIN_LENGTH_STREET + " and "
			+ MAX_LENGTH_STREET + " symbols.";

	public static final int MIN_LENGTH_STREET_NUMBER = 1;
	public static final int MAX_LENGTH_STREET_NUMBER = 10;
	public static final String EMPTY_STREET_NUMBER = "Street number is required";
	public static final String STREET_NUMBER_OUT_OF_BOUNDS = "Street number must be between " + MIN_LENGTH_STREET_NUMBER
			+ " and " + MAX_LENGTH_STREET_NUMBER + " symbols.";

	public static final int MIN_LENGTH_APARTMENT_BUILDING_NUMBER = 1;
	public static final int MAX_LENGTH_APARTMENT_BUILDING_NUMBER = 10;
	public static final String EMPTY_APARTMENT_BUILDING_NUMBER = "Apartment building number is required";
	public static final String APARTMENT_BUILDING_NUMBER_OUT_OF_BOUNDS = "Apartment building number must be between "
			+ MIN_LENGTH_APARTMENT_BUILDING_NUMBER + " and " + MAX_LENGTH_APARTMENT_BUILDING_NUMBER + " symbols.";

	public static final int MIN_LENGTH_ENTRANCE = 1;
	public static final int MAX_LENGTH_ENTRANCE = 10;
	public static final String EMPTY_ENTRANCE = "Entrance is required";
	public static final String ENTRANCE_OUT_OF_BOUNDS = "Apartment building number must be between "
			+ MIN_LENGTH_ENTRANCE + " and " + MAX_LENGTH_ENTRANCE + " symbols.";

	public static final int MIN_LENGTH_APARTMENT_NUMBER = 1;
	public static final int MAX_LENGTH_APARTMENT_NUMBER = 10;
	public static final String EMPTY_APARTMENT_NUMBER = "Apartment number is required";
	public static final String APARTMENT_NUMBER_OUT_OF_BOUNDS = "Apartment number must be between "
			+ MIN_LENGTH_APARTMENT_NUMBER + " and " + MAX_LENGTH_APARTMENT_NUMBER + " symbols.";

	public static final String FLOOR_NEGATIVE = "Floor cannot be negative";

	public static final String EMPTY_AUTHORITY = "Authority is required";

	public static final String ONLY_CURRENT_CUSTOMER_SECURITY_EXPRESSION = "hasAuthority('ROLE_CUSTOMER') and principal.id == #customerId";

	public static final String ONLY_CURRENT_RESTAURANT_SECURITY_EXPRESSION = "hasAuthority('ROLE_RESTAURANT') and principal.id == #restaurantId";

	private ValidationConstraints() {
	}
}
