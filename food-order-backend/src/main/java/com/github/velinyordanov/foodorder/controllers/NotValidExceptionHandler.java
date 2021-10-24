package com.github.velinyordanov.foodorder.controllers;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.github.velinyordanov.foodorder.exceptions.BadRequestException;
import com.github.velinyordanov.foodorder.exceptions.DuplicateCategoryException;
import com.github.velinyordanov.foodorder.exceptions.DuplicateCustomerException;
import com.github.velinyordanov.foodorder.exceptions.DuplicateUserException;
import com.github.velinyordanov.foodorder.exceptions.ExistingDiscountCodeException;
import com.github.velinyordanov.foodorder.exceptions.ExistingUnfinishedOrderException;
import com.github.velinyordanov.foodorder.exceptions.ForeignCategoryException;
import com.github.velinyordanov.foodorder.exceptions.NonEmptyCategoryException;
import com.github.velinyordanov.foodorder.exceptions.NotFoundException;
import com.github.velinyordanov.foodorder.exceptions.UnrecognizedCategoriesException;

@ControllerAdvice
public class NotValidExceptionHandler extends ResponseEntityExceptionHandler {
	private static final Log LOGGER = LogFactory.getLog(NotValidExceptionHandler.class);

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("title", "Validation errors");

		String errors = ex.getBindingResult()
				.getAllErrors()
				.stream()
				.map(x -> x.getDefaultMessage())
				.collect(Collectors.joining(", "));

		body.put("description", errors);

		return new ResponseEntity<>(body, headers, status);
	}

	@ExceptionHandler({ DisabledException.class, LockedException.class })
	public ResponseEntity<Map<String, String>> handleSecurityException(Exception ex) {
		return this.buildResponse(ex, "User disabled or locked", HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException ex) {
		return this.buildResponse(ex, "User not found", HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
		return this.buildResponse(ex, "Acces is denied", HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(DuplicateUserException.class)
	public ResponseEntity<Map<String, String>> handleUserDuplicate(DuplicateUserException ex) {
		return this.buildResponse(ex, "User already exists", HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DuplicateCategoryException.class)
	public ResponseEntity<Map<String, String>> handleDuplicateCategory(DuplicateCategoryException ex) {
		return this.buildResponse(ex, "Category already exists", HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NonEmptyCategoryException.class)
	public ResponseEntity<Map<String, String>> handleNonEmptyCategoryException(NonEmptyCategoryException ex) {
		return this.buildResponse(ex, "Category has foods associated with it", HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException ex) {
		return this.buildResponse(ex, "Entity not found", HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ExistingDiscountCodeException.class)
	public ResponseEntity<Map<String, String>> handleExistingDiscountCodeException(ExistingDiscountCodeException ex) {
		return this.buildResponse(ex, "Discount code already exists for restaurant", HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<Map<String, String>> handleBadRequestException(BadRequestException ex) {
		return this.buildResponse(ex, "Bad request", HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(UnrecognizedCategoriesException.class)
	public ResponseEntity<Map<String, String>> handleUnrecognizedCategoriesException(
			UnrecognizedCategoriesException ex) {
		return this.buildResponse(ex, "Unrecognized categories were provided", HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ForeignCategoryException.class)
	public ResponseEntity<Map<String, String>> handleForeignCategoryException(ForeignCategoryException ex) {
		return this.buildResponse(ex, "Category not belonging to restuarant was provided", HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ExistingUnfinishedOrderException.class)
	public ResponseEntity<Map<String, String>> handleExistingUnfinishedOrderException(
			ExistingUnfinishedOrderException ex) {
		return this.buildResponse(ex, "You have a pending or accepted order already.", HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DuplicateCustomerException.class)
	public ResponseEntity<Map<String, String>> handleDuplicateCustomerException(DuplicateCustomerException ex) {
		return this.buildResponse(ex, "Customer already exists.", HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Throwable.class)
	public ResponseEntity<Map<String, String>> handleGenericException(Throwable ex) {
		LOGGER.error("Internal server error", ex);

		return this.buildResponse(ex, "An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private ResponseEntity<Map<String, String>> buildResponse(Throwable ex, String title, HttpStatus httpStatus) {
		Map<String, String> body = new HashMap<String, String>();
		body.put("title", title);
		body.put("description", ex.getLocalizedMessage());

		if (httpStatus.is5xxServerError()) {
			body.put("description", "An error occurred. Try again later.");
		}

		return ResponseEntity.status(httpStatus).body(body);
	}
}
