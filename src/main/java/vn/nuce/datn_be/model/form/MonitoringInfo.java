package vn.nuce.datn_be.model.form;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import vn.nuce.datn_be.anotation.validation.MonitoringStatus;

@Getter
@Setter
public class MonitoringInfo {
    String violationError;      // lỗi vi phạm
    String violationCode;       // mã lỗi vi phạm
    String violationInfo;       // bằng chứng lỗi vi phạm
    @NotNull
    @MonitoringStatus
    String monitoringStatus;
    @NotNull
    @vn.nuce.datn_be.anotation.validation.MultipartFile
    MultipartFile screenShotImg;
    @vn.nuce.datn_be.anotation.validation.MultipartFile
    MultipartFile faceImg;
    String candidateId;
    Long numberId;
}
