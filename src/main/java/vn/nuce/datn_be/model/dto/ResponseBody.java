package vn.nuce.datn_be.model.dto;

import lombok.Getter;
import lombok.Setter;
import vn.nuce.datn_be.model.enumeration.ResponseCode;

@Getter
@Setter
public class ResponseBody {
    Boolean success;
    ResponseCode code;
    String message;
    Object data;

    public ResponseBody(Boolean success, ResponseCode code, String message, Object data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ResponseBody responseBodySuccess(Object data) {
        return new ResponseBody(true, ResponseCode.SUCCESS, ResponseCode.SUCCESS.getName(), data);
    }

    public static ResponseBody responseBodyFail(String message) {
        return new ResponseBody(false, ResponseCode.FAIL, message, null);
    }
}
