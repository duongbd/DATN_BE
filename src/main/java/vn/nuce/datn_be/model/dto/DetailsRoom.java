package vn.nuce.datn_be.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import vn.nuce.datn_be.enity.App;
import vn.nuce.datn_be.enity.Room;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class DetailsRoom {

    private Date startTime;
    private Date endTime;
    private List<String> apps = new LinkedList<>();
    private String name;
    private String urls;
    private String status;
    private Integer classSize;
    private Long roomId;

    public DetailsRoom() {
    }

    public DetailsRoom(Room room) {
        this.setEndTime(room.getEndTime());
        this.setStartTime(room.getStartTime());
        if (room.getRoomAppKeys() != null) {
            room.getRoomAppKeys().forEach(roomAppKey -> this.getApps().add(roomAppKey.getApp().getAppName()));
        }
        this.setName(room.getName());
        this.setUrls(room.getUrls());
        this.setStatus(room.getRoomStatus().getName());
        this.setClassSize(room.getCandidateInfos() != null ? room.getCandidateInfos().size() : 0);
        this.setRoomId(room.getId());
    }
}
