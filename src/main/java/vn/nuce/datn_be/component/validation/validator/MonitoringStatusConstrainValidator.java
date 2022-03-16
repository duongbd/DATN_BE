package vn.nuce.datn_be.component.validation.validator;

import vn.nuce.datn_be.component.validation.anotation.MonitoringStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MonitoringStatusConstrainValidator implements ConstraintValidator<MonitoringStatus, String> {

    public boolean isValid(String s, ConstraintValidatorContext cvc) {
        if (s == null) {
            return false;
        }
        return vn.nuce.datn_be.model.enumeration.MonitoringStatus.getMonitoringStatusByName(s) != null;
    }
}
