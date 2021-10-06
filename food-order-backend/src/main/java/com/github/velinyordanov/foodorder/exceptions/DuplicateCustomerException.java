package com.github.velinyordanov.foodorder.exceptions;

public class DuplicateCustomerException extends RuntimeException {
	private static final long serialVersionUID = 7834037514100080175L;

	public DuplicateCustomerException(String message) {
		super(message);
	}
}
