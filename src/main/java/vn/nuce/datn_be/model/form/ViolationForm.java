package vn.nuce.datn_be.model.form;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.nuce.datn_be.anotation.validation.MonitoringStatus;

@Getter
@Setter
public class ViolationForm {
    String violationError;      // lỗi vi phạm
    String violationCode;       // mã lỗi vi phạm
    String violationInfo;       // bằng chứng lỗi vi phạm
    @NotNull
    @MonitoringStatus
    String monitoringStatus;
    String candidateId;
    Long numberId;
}
