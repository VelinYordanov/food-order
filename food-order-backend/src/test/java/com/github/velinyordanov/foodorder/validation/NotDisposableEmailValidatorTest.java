package com.github.velinyordanov.foodorder.validation;

import static org.mockito.BDDMockito.given;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.github.velinyordanov.foodorder.dto.DisposableEmailValidationApiResponse;

@ExtendWith(MockitoExtension.class)
public class NotDisposableEmailValidatorTest {
	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private NotDisposableEmailValidator notDisposableEmailValidator;

	@Test
	public void isValidShould_returnFalse_whenTheApiReturnsThatTheEmailIsDisposable() {
		DisposableEmailValidationApiResponse disposableEmailValidationApiResponse = new DisposableEmailValidationApiResponse();
		disposableEmailValidationApiResponse.setDisposable("true");
		
		given(this.restTemplate.getForObject(
				any(),
				eq(DisposableEmailValidationApiResponse.class),
				eq(Collections.singletonMap("email", "testEmail"))))
						.willReturn(disposableEmailValidationApiResponse);

		assertFalse(this.notDisposableEmailValidator.isValid("testEmail", null));
	}
	
	@Test
	public void isValidShould_returnTrue_whenTheApiReturnsThatTheEmailIsNotDisposable() {
		DisposableEmailValidationApiResponse disposableEmailValidationApiResponse = new DisposableEmailValidationApiResponse();
		disposableEmailValidationApiResponse.setDisposable("false");
		
		given(this.restTemplate.getForObject(
				any(),
				eq(DisposableEmailValidationApiResponse.class),
				eq(Collections.singletonMap("email", "testEmail"))))
						.willReturn(disposableEmailValidationApiResponse);

		assertTrue(this.notDisposableEmailValidator.isValid("testEmail", null));
	}
	
	@Test
	public void isValidShould_returnTrue_whenAnErrorOccursWithTheRequest() {
		given(this.restTemplate.getForObject(
				any(),
				eq(DisposableEmailValidationApiResponse.class),
				eq(Collections.singletonMap("email", "testEmail"))))
						.willThrow(RestClientException.class);

		assertTrue(this.notDisposableEmailValidator.isValid("testEmail", null));
	}
}
