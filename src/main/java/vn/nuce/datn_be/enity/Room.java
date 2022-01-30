package vn.nuce.datn_be.enity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import vn.nuce.datn_be.model.enumeration.RoomStatus;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "ROOM")
@Getter
@Setter
public class Room extends BaseEntity {
    @Column
    String urls;

    @Column
    String name;

    @Column
    String processAccess;

    @Column(name = "START_TIME", nullable = false)
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @Column(name = "END_TIME", nullable = false)
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;

    @ManyToOne
    @JoinColumn(name = "USER_FK", nullable = false, insertable = false,updatable = false)
    private User ownerId;

    @Column(name ="USER_FK")
    private Long userFk;

    @OneToMany(mappedBy = "room")
    List<LogTime> logTimes;

    @OneToMany(mappedBy = "roomId")
    List<Message> messages;

    @OneToMany(mappedBy = "roomId")
    List<RoomShares> roomShares;

    @Column(name = "ROOM_STATUS")
    @Enumerated(EnumType.STRING)
    private RoomStatus roomStatus;

    @OneToMany(mappedBy = "room")
    Set<CandidateInfo> candidateInfos;
}
