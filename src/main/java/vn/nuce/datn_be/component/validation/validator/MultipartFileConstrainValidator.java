package vn.nuce.datn_be.component.validation.validator;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MultipartFileConstrainValidator implements ConstraintValidator<vn.nuce.datn_be.component.validation.anotation.MultipartFile, MultipartFile> {

    public boolean isValid(MultipartFile file, ConstraintValidatorContext cvc) {
        return !file.isEmpty();
    }
}
