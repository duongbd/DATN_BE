package vn.nuce.datn_be.model.enumeration;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum ErrorStatus {
    UNHANDLED_ERROR(10001, "Service is currently unavailable. Please try again later", "error.unhandled-error");

    private final String u;
    public final String key;
    private final Object[] v;
    private final int code;

    private ErrorStatus(final int code, final String u, final String key) {
        this.u = u;
        this.code = code;
        this.key = key;
        this.v = null;
    }

    private ErrorStatus(final int code, final String u, final String key, final Object[] v) {
        this.u = u;
        this.key = key;
        this.v = v;
        this.code = code;
    }

    public static ErrorStatus getErrorStatusByName(int code) {
        for (ErrorStatus errorStatus : ErrorStatus.values()) {
            if (code == errorStatus.code) {
                return errorStatus;
            }
        }
        return null;
    }
}
