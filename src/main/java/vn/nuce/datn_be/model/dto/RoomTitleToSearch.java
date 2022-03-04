package vn.nuce.datn_be.model.dto;

import lombok.Getter;
import lombok.Setter;
import vn.nuce.datn_be.enity.Room;

@Getter
@Setter
public class RoomTitleToSearch {
    Long roomId;
    String roomName;

    public RoomTitleToSearch(Room room) {
        this.roomId = room.getId();
        this.roomName = room.getName();
    }
}
