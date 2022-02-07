package vn.nuce.datn_be.enity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "ROOM_APP_KEY")
public class RoomAppKey extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "ROOM_FK", referencedColumnName = "id", updatable = false, insertable = false)
    private Room room;

    @Column(name = "ROOM_FK")
    private Long roomFk;

    @ManyToOne
    @JoinColumn(name = "APP_FK", referencedColumnName = "id", updatable = false, insertable = false)
    private App app;

    @Column(name = "APP_FK")
    private Long appFk;
}
