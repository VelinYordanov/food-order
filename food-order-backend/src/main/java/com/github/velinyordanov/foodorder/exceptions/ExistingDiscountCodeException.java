package com.github.velinyordanov.foodorder.exceptions;

public class ExistingDiscountCodeException extends RuntimeException {
	private static final long serialVersionUID = -4633420045088535857L;

	public ExistingDiscountCodeException(String message) {
		super(message);
	}
}
