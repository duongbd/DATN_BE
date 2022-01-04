package vn.nuce.datn_be.enity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "MESSAGE")
@Getter
@Setter
public class Message extends BaseEntity {
    @Column(name = "FROM_IP")
    String fromIP;

    @Column(name = "TO_IP")
    String toIP;

    @Column(name="FROM_MAC")
    String fromMAC;

    @Column(name="TO_MAC")
    String toMAC;

    @Column(name="SENDER")
    String sender;

    @Column(name = "RECEIVER")
    String receiver;

    @Column(name = "CONTENT")
    String content;

    @Column(name = "TIME_CREATE", nullable = false)
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss:SSS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeCreate;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room roomId;
}
