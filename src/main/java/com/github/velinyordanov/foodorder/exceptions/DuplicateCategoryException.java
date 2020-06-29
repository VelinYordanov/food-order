package com.github.velinyordanov.foodorder.exceptions;

public class DuplicateCategoryException extends RuntimeException {
    private static final long serialVersionUID = 7685664875217686416L;

    public DuplicateCategoryException(String message) {
	super(message);
    }
}
