package vn.nuce.datn_be.model.form;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import vn.nuce.datn_be.anotation.validation.MonitoringStatus;

@Getter
@Setter
public class MonitoringInfo {
    @NotNull
    @vn.nuce.datn_be.anotation.validation.MultipartFile
    MultipartFile screenShotImg;
    @vn.nuce.datn_be.anotation.validation.MultipartFile
    MultipartFile faceImg;
    String candidateId;
    Long numberId;
}
