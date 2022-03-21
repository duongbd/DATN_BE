package vn.nuce.datn_be.enity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import vn.nuce.datn_be.model.enumeration.RoomStatus;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "ROOM")
@Getter
@Setter
public class Room extends BaseEntity {
    @Column
    @NotBlank
    String urls;

    @Column
    @NotBlank
    String name;

    @Column(name = "START_TIME", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @Column(name = "END_TIME", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;

    @ManyToOne
    @JoinColumn(name = "USER_FK", nullable = false, insertable = false, updatable = false)
    private User owner;

    @Column(name = "USER_FK")
    private Long ownerFk;

    @OneToMany(mappedBy = "room")
    List<LogTime> logTimes;

    @OneToMany(mappedBy = "room")
    List<Message> messages;

    @OneToMany(mappedBy = "room")
    List<RoomShares> roomShares;

    @Column(name = "ROOM_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomStatus roomStatus;

    @OneToMany(mappedBy = "room", fetch = FetchType.EAGER)
    Set<CandidateInfo> candidateInfos;

    @OneToMany(mappedBy = "room")
    private List<RoomAppKey> roomAppKeys;
}
