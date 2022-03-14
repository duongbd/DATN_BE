package vn.nuce.datn_be.model.form;

import lombok.Getter;
import lombok.Setter;
import vn.nuce.datn_be.enity.CandidateInfo;
import vn.nuce.datn_be.enity.Room;
import vn.nuce.datn_be.model.enumeration.CandidateStatus;
import vn.nuce.datn_be.model.enumeration.RoomStatus;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
public class NotifyRoomStatus {
    Long roomId;

    @Enumerated(EnumType.STRING)
    RoomStatus roomStatus;

    public static NotifyRoomStatus notifyRoomStatus(Room room) {
        NotifyRoomStatus notifyRoomStatus = new NotifyRoomStatus();
        notifyRoomStatus.setRoomId(room.getId());
        notifyRoomStatus.setRoomStatus(room.getRoomStatus());
        return notifyRoomStatus;
    }
}
