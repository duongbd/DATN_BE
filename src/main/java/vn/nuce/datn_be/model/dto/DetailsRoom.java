package vn.nuce.datn_be.model.dto;

import lombok.Getter;
import lombok.Setter;
import vn.nuce.datn_be.enity.Room;

import java.util.Date;

@Getter
@Setter
public class DetailsRoom {

    private Date startTime;
    private Date endTime;
    private Long ownerId;
    private String processAccess;
    private String name;
    private String urls;

    public DetailsRoom() {
    }

    public DetailsRoom(Room room) {
        this.setEndTime(room.getEndTime());
        this.setStartTime(room.getStartTime());
        this.setOwnerId(room.getUserFk());
        this.setProcessAccess(room.getProcessAccess());
        this.setName(room.getName());
        this.setUrls(room.getUrls());
    }
}
