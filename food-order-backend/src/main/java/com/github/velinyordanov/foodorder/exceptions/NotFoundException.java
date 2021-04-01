package com.github.velinyordanov.foodorder.exceptions;

public class NotFoundException extends RuntimeException {
	private static final long serialVersionUID = -6613180942529207627L;

	public NotFoundException(String message) {
		super(message);
	}
}
