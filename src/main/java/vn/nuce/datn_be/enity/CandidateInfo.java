package vn.nuce.datn_be.enity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import vn.nuce.datn_be.model.enumeration.CandidateStatus;
import vn.nuce.datn_be.utils.RandomIdGenerator;

import javax.persistence.*;
import javax.validation.constraints.Email;

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

    @Column(name = "PASSWORD")
    String password;

    @Column(name = "INFO")
    String info;

    @Column(name = "CANDIDATE_STATUS")
    CandidateStatus candidateStatus;

    @ManyToOne
    @JoinColumn(name = "ROOM_FK", nullable = false, insertable = false, updatable = false)
    private Room room;

    @Column(name = "ROOM_FK")
    private Long roomFk;
}
