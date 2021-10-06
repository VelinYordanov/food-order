package com.github.velinyordanov.foodorder.exceptions;

public class UnrecognizedCategoriesException extends RuntimeException {
	private static final long serialVersionUID = -3035868671910508785L;

	public UnrecognizedCategoriesException(String message) {
		super(message);
	}
}
