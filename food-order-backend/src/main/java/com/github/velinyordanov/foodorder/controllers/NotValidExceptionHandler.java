package com.github.velinyordanov.foodorder.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import com.github.velinyordanov.foodorder.exceptions.DuplicateCategoryException;
import com.github.velinyordanov.foodorder.exceptions.DuplicateUserException;
import com.github.velinyordanov.foodorder.exceptions.NonEmptyCategoryException;
import com.github.velinyordanov.foodorder.exceptions.NotFoundException;

@ControllerAdvice
public class NotValidExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
	    HttpHeaders headers,
	    HttpStatus status,
	    WebRequest request) {

	Map<String, Object> body = new LinkedHashMap<>();
	body.put("timestamp", new Date());
	body.put("status", status.value());

	List<String> errors = ex.getBindingResult()
		.getFieldErrors()
		.stream()
		.map(x -> x.getDefaultMessage())
		.collect(Collectors.toList());

	body.put("errors", errors);

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
	return this.buildResponse(ex, "Acces is denied", HttpStatus.FORBIDDEN);
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
	return this.buildResponse(ex, "Category has foods associated with it", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException ex) {
	return this.buildResponse(ex, "Entity not found", HttpStatus.NOT_FOUND);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
//	ex.printStackTrace();
//	return this.buildResponse(ex, "An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    private ResponseEntity<Map<String, String>> buildResponse(Exception ex, String title, HttpStatus httpStatus) {
	Map<String, String> body = new HashMap<String, String>();
	body.put("title", title);
	body.put("description", ex.getLocalizedMessage());

	return ResponseEntity.status(httpStatus).body(body);
    }
}
