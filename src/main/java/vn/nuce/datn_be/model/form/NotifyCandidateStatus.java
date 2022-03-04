package vn.nuce.datn_be.model.form;


import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import lombok.Getter;
import lombok.Setter;
import vn.nuce.datn_be.model.enumeration.CandidateStatus;

@Getter
@Setter
public class NotifyCandidateStatus {
    String candidateId;

    Long numberId;

    @Enumerated(EnumType.STRING)
    CandidateStatus candidateStatus;
}
