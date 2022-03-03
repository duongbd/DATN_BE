package vn.nuce.datn_be.model.form;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import vn.nuce.datn_be.model.enumeration.MonitoringStatus;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class MonitoringInfo {
    String violationError;      // lỗi vi phạm
    String violationCode;       // mã lỗi vi phạm
    String violationInfo;       // bằng chứng lỗi vi phạm
    @Enumerated(EnumType.STRING)
    @NotBlank
    MonitoringStatus monitoringStatus;
    @NotNull
    MultipartFile file;
}
