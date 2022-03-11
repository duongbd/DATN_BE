package vn.nuce.datn_be.model.form;

import lombok.Getter;
import lombok.Setter;
import vn.nuce.datn_be.model.enumeration.RoomStatus;

@Getter
@Setter
public class RoomSearchForm {
    private String keyName;
    private RoomStatus roomStatus;
    private String startDate;
    private Long monitorId;
}
