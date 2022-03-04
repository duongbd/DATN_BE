package vn.nuce.datn_be.model.dto;

import lombok.Getter;
import lombok.Setter;
import vn.nuce.datn_be.model.enumeration.CandidateStatus;

@Getter
@Setter
public class CandidateUrlScreenShotForm {
    String fileId;
    CandidateStatus status;
    String candidateId;
    Long numberId;
}
