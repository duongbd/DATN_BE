package vn.nuce.datn_be.enity;

import lombok.Getter;
import lombok.Setter;
import vn.nuce.datn_be.model.enumeration.CandidateStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "CANDIDATE_INFO")
@Getter
@Setter
public class CandidateInfo extends BaseEntity {
    @Column(unique = true)
    Long numberId;
    @Column
    String candidateName;
    @Column
    String email;
    @Column
    String password;
    @Column
    String info;
    @Transient
    CandidateStatus candidateStatus;
}
