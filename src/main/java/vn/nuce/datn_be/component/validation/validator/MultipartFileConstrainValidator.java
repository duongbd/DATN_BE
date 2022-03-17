package vn.nuce.datn_be.component.validation.validator;

import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Locale;
import java.util.Objects;

public class MultipartFileConstrainValidator implements ConstraintValidator<vn.nuce.datn_be.component.validation.anotation.MultipartFile, MultipartFile> {

    public boolean isValid(MultipartFile file, ConstraintValidatorContext cvc) {
        if (file == null) {
            return false;
        }
        if (!Objects.requireNonNull(file.getOriginalFilename()).toLowerCase(Locale.ROOT).contains(".png")) {
            return !file.isEmpty();
        }
        return false;
    }
}
