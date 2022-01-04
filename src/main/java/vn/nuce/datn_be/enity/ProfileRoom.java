package vn.nuce.datn_be.enity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "PROFILE_ROOM")
@Getter
@Setter
public class ProfileRoom extends BaseEntity {
    @Column
    String urls;

    @Column
    String name;

    @Column
    String ProcessAccess;

    @Column(name = "START_TIME", nullable = false)
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @Column(name = "END_TIME", nullable = false)
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User ownerId;

}
