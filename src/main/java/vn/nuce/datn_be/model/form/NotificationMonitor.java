package vn.nuce.datn_be.model.form;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationMonitor {
    String violationError;      // lỗi vi phạm
    String violationCode;       // mã lỗi vi phạm
    String violationInfo;       // bằng chứng lỗi vi phạm
    String monitoringStatus;
    String candidateId;
    Long numberId;

    public NotificationMonitor(ViolationForm info) {
        this.monitoringStatus = info.getMonitoringStatus();
        this.violationError = info.getViolationError() != null ? info.getViolationError() : "";
        this.violationCode = info.getViolationInfo() != null ? info.getViolationCode() : "";
        this.violationInfo = info.getViolationInfo() != null ? info.getViolationInfo() : "";
        this.candidateId = info.getCandidateId();
        this.numberId = info.getNumberId();
    }
}
