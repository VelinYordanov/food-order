package com.github.velinyordanov.foodorder.exceptions;

public class NonEmptyCategoryException extends RuntimeException {
    private static final long serialVersionUID = 3152752449069672083L;

    public NonEmptyCategoryException(String message) {
	super(message);
    }
}
