package com.github.velinyordanov.foodorder.validation.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.github.velinyordanov.foodorder.validation.NotDisposableEmailValidator;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { NotDisposableEmailValidator.class })
public @interface NotDisposableEmail {
	String message() default "Disposable emails are not allowed.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
