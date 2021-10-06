package com.github.velinyordanov.foodorder.exceptions;

public class ForeignCategoryException extends RuntimeException {
	private static final long serialVersionUID = -3274732328711843360L;
	
	public ForeignCategoryException(String message) {
		super(message);
	}
}
