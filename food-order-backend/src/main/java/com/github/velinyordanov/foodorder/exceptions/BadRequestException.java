package com.github.velinyordanov.foodorder.exceptions;

public class BadRequestException extends RuntimeException {
    private static final long serialVersionUID = 7598666508027174839L;

    public BadRequestException(String message) {
	super(message);
    }
}
