package com.github.velinyordanov.foodorder.exceptions;

public class ExistingUnfinishedOrderException extends RuntimeException {
	private static final long serialVersionUID = -7532804083970731095L;
	
	public ExistingUnfinishedOrderException(String message) {
		super(message);
	}
}
