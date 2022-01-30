package vn.nuce.datn_be.model.enumeration;

import lombok.Getter;

@Getter
public enum ResponseCode {
    SUCCESS("SUCCESS"),
    FAIL("FAIL"),
    UNKNOW("UNKNOW");

    private final String name;

    ResponseCode(String name) {
        this.name = name;
    }

    public static ResponseCode getRoomStatusByName(String name) {
        for (ResponseCode responseCode : ResponseCode.values()) {
            if (name.equalsIgnoreCase(responseCode.getName())) {
                return responseCode;
            }
        }
        return null;
    }
}
