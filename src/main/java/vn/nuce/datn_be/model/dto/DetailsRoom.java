package vn.nuce.datn_be.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import vn.nuce.datn_be.enity.App;
import vn.nuce.datn_be.enity.Room;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class DetailsRoom {

    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private List<String> apps = new LinkedList<>();
    private String name;
    private String urls;
    private String status;
    private Integer classSize;
    private Long roomId;

    public DetailsRoom() {
    }

    public DetailsRoom(Room room, boolean candidate) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.setStartDate(dateFormat.format(room.getStartTime()));
        this.setEndDate(dateFormat.format(room.getEndTime()));
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        this.setStartTime(timeFormat.format(room.getStartTime()));
        this.setEndTime(timeFormat.format(room.getEndTime()));
        if (room.getRoomAppKeys() != null) {
            if (candidate) {
                room.getRoomAppKeys().forEach(roomAppKey -> this.getApps().add(roomAppKey.getApp().getProcessName()));
            } else {
                room.getRoomAppKeys().forEach(roomAppKey -> this.getApps().add(roomAppKey.getApp().getAppName()));
            }
        }
        this.setName(room.getName());
        this.setUrls(room.getUrls());
        this.setStatus(room.getRoomStatus().getName());
        this.setClassSize(room.getCandidateInfos() != null ? room.getCandidateInfos().size() : 0);
        this.setRoomId(room.getId());
    }
}
