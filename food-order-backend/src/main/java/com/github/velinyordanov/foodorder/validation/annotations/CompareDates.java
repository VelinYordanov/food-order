package com.github.velinyordanov.foodorder.validation.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.github.velinyordanov.foodorder.validation.CompareDatesValidator;

@Constraint(validatedBy = CompareDatesValidator.class)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CompareDates {
  String message() default "After date must be after before date";
  Class <?> [] groups() default {};
  Class <? extends Payload> [] payload() default {};
  
  String before();
  
  String after();
}
