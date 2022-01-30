package vn.nuce.datn_be.enity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ROOM_SHARES")
@Getter
@Setter
public class RoomShares extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "ROOM_ID")
    private Room roomId;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User watcherId;
}
