package vn.nuce.datn_be.model.dto;

import lombok.Getter;
import lombok.Setter;
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
    private Long ownerId;
    private List<String> apps = new LinkedList<>();
    private String name;
    private String urls;
    private String status;

    public DetailsRoom() {
    }

    public DetailsRoom(Room room) {
        this.setEndTime(room.getEndTime());
        this.setStartTime(room.getStartTime());
        this.setOwnerId(room.getUserFk());
        room.getRoomAppKeys().forEach(roomAppKey -> this.getApps().add(roomAppKey.getApp().getAppName()));
        this.setName(room.getName());
        this.setUrls(room.getUrls());
        this.setStatus(room.getRoomStatus().getName());
    }
}
