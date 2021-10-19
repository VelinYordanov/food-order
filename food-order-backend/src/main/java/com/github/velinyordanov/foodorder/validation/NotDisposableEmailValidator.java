package com.github.velinyordanov.foodorder.validation;

import java.text.MessageFormat;
import java.util.Collections;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.github.velinyordanov.foodorder.dto.DisposableEmailValidationApiResponse;
import com.github.velinyordanov.foodorder.validation.annotations.NotDisposableEmail;

@Component
public class NotDisposableEmailValidator implements ConstraintValidator<NotDisposableEmail, String> {
	private static final Log LOGGER = LogFactory.getLog(NotDisposableEmailValidator.class);

	private final RestTemplate restTemplate;

	@Value("${disposable.email.validation.url}")
	private String url;

	public NotDisposableEmailValidator(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		LOGGER.info(MessageFormat.format("Checking if email {0} is disposable", value));

		try {
			DisposableEmailValidationApiResponse response = this.restTemplate.getForObject(this.url,
					DisposableEmailValidationApiResponse.class, Collections.singletonMap("email", value));

			return "false".equals(response.getDisposable());
		} catch (RestClientException e) {
			LOGGER.warn("An error occurred with the request for disposable email validation for email " + value);
			// Do not want to stop registration if api is offline
			return true;
		}
	}

}
