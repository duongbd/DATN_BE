package vn.nuce.datn_be.component.validation.anotation;

import vn.nuce.datn_be.component.validation.validator.MonitoringStatusConstrainValidator;
import vn.nuce.datn_be.component.validation.validator.MultipartFileConstrainValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = MultipartFileConstrainValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MultipartFile {
    //error message
    public String message() default "File size must bigger than zero";

    //represents group of constraints
    public Class<?>[] groups() default {};

    //represents additional information about annotation
    public Class<? extends Payload>[] payload() default {};
}
