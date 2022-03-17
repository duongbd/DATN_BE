package vn.nuce.datn_be.model.form;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import vn.nuce.datn_be.model.enumeration.MonitoringStatus;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class MonitoringInfo {
    String violationError;      // lỗi vi phạm
    String violationCode;       // mã lỗi vi phạm
    String violationInfo;       // bằng chứng lỗi vi phạm
    @NotNull
    @vn.nuce.datn_be.component.validation.anotation.MonitoringStatus
    String monitoringStatus;
    @NotNull
    @vn.nuce.datn_be.component.validation.anotation.MultipartFile
    MultipartFile screenShotImg;
    @vn.nuce.datn_be.component.validation.anotation.MultipartFile
    MultipartFile faceImg;
}
