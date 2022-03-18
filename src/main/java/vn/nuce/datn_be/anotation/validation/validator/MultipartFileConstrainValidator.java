package vn.nuce.datn_be.anotation.validation.validator;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Locale;
import java.util.Objects;

public class MultipartFileConstrainValidator implements ConstraintValidator<vn.nuce.datn_be.anotation.validation.MultipartFile, MultipartFile> {

    public boolean isValid(MultipartFile file, ConstraintValidatorContext cvc) {
        if (file == null) {
            return false;
        }
        if (Objects.requireNonNull(file.getOriginalFilename()).toLowerCase(Locale.ROOT).matches(".*\\.png$")) {
            return !file.isEmpty();
        }
        return false;
    }
}
