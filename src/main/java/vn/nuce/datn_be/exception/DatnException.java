package vn.nuce.datn_be.exception;

import lombok.Getter;
import lombok.Setter;
import vn.nuce.datn_be.model.enumeration.ErrorStatus;

import java.util.Map;

@Getter
@Setter
public class DatnException extends RuntimeException{
    private ErrorStatus errorStatus;

    private String errorMessage;

    private Map<String, Object> details;

    public DatnException(ErrorStatus errorStatus) {
        super();
        this.errorStatus = errorStatus;
    }

    public DatnException(ErrorStatus errorStatus, Map<String, Object> details) {
        super();
        this.errorStatus = errorStatus;
        this.details = details;
    }

    public DatnException(String errorMessage) {
        super();
        this.errorMessage = errorMessage;
    }

    public ErrorStatus getErrorStatus() {
        return errorStatus;
    }
}
