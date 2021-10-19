package com.github.velinyordanov.foodorder.validation;

import java.lang.reflect.Field;
import java.time.LocalDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.velinyordanov.foodorder.validation.annotations.CompareDates;

public class CompareDatesValidator implements ConstraintValidator<CompareDates, Object> {
	private static final Log LOGGER = LogFactory.getLog(CompareDatesValidator.class);

	private String beforeFieldName;
	private String afterFieldName;

	@Override
	public void initialize(final CompareDates constraintAnnotation) {
		beforeFieldName = constraintAnnotation.before();
		afterFieldName = constraintAnnotation.after();
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		try {
			final Field beforeDateField = value.getClass().getDeclaredField(beforeFieldName);
			beforeDateField.setAccessible(true);

			final Field afterDateField = value.getClass().getDeclaredField(afterFieldName);
			afterDateField.setAccessible(true);

			final LocalDate beforeDate = (LocalDate) beforeDateField.get(value);
			final LocalDate afterDate = (LocalDate) afterDateField.get(value);

			return (beforeDate != null && afterDate != null) && afterDate.isAfter(beforeDate);
		} catch (final Exception e) {
			LOGGER.error("An error occurred while validating dates.", e);

			return false;
		}
	}
}
