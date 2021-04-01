package com.github.velinyordanov.foodorder.exceptions;

public class DuplicateUserException extends RuntimeException {
	private static final long serialVersionUID = -538768814688591421L;

	public DuplicateUserException(String message) {
		super(message);
	}
}
