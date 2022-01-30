package vn.nuce.datn_be.enity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "LOG_TIME")
@Getter
@Setter
public class LogTime extends BaseEntity {
    @Column
    String content;

    @Column(name = "TIME_CREATE", nullable = false)
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss:SSS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeCreate;

    @ManyToOne
    @JoinColumn(name = "ROOM_FK", nullable = false)
    private Room room;
}
