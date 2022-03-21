package vn.nuce.datn_be.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import vn.nuce.datn_be.enity.Room;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class RoomForm {
    @NotBlank
    String urls;

    @NotBlank
    private String startDate;

    @NotBlank
    private String endDate;

    @NotBlank
    private String startTime;

    @NotBlank
    private String endTime;

    @NotBlank
    private String name;

    private List<String> apps = new LinkedList<>();

    private MultipartFile file;
}
