package vn.nuce.datn_be.enity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;
import vn.nuce.datn_be.model.enumeration.CandidateStatus;
import vn.nuce.datn_be.model.enumeration.SendMailStatus;
import vn.nuce.datn_be.utils.RandomIdGenerator;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity
@Table(name = "CANDIDATE_INFO")
@Getter
@Setter
public class CandidateInfo {

    @Id
    @GeneratedValue(generator = RandomIdGenerator.generatorName)
    @GenericGenerator(name = RandomIdGenerator.generatorName, strategy = "vn.nuce.datn_be.utils.RandomIdGenerator")
    @Column(name = "ID")
    String id;

    @Column(name = "NUMBER_ID")
    Long numberId;

    @Column(name = "CANDIDATE_NAME")
    String candidateName;

    @Column(name = "EMAIL")
    @Email
    String email;

    @JsonIgnore
    @Column(name = "PASSWORD")
    String password;

    @Column(name = "INFO")
    String info;

    @Enumerated(EnumType.STRING)
    @Column(name = "CANDIDATE_STATUS")
    CandidateStatus candidateStatus = CandidateStatus.OFFLINE;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "ROOM_FK", nullable = false, insertable = false, updatable = false)
    private Room room;

    @Column(name = "ROOM_FK")
    private Long roomFk;

    @Column(name = "NEWEST_SCREENSHOT_ID")
    private String newestScreenShotId;

    @Column(name = "LAST_SAW")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastSaw;

    @Enumerated(EnumType.STRING)
    @Column(name = "SEND_MAIL_STATUS")
    private SendMailStatus sendMailStatus = SendMailStatus.UNSENT;
}
