package vn.nuce.datn_be.model.form;


import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import lombok.Getter;
import lombok.Setter;
import vn.nuce.datn_be.enity.CandidateInfo;
import vn.nuce.datn_be.model.enumeration.CandidateStatus;

@Getter
@Setter
public class NotifyCandidateStatus {
    String candidateId;

    Long numberId;

    @Enumerated(EnumType.STRING)
    CandidateStatus candidateStatus;

    public static NotifyCandidateStatus notifyCandidateStatusDisconnected(CandidateInfo candidateInfo) {
        NotifyCandidateStatus notifyCandidateStatus = new NotifyCandidateStatus();
        notifyCandidateStatus.setCandidateId(candidateInfo.getId());
        notifyCandidateStatus.setNumberId(candidateInfo.getNumberId());
        notifyCandidateStatus.setCandidateStatus(CandidateStatus.DISCONNECTED);
        return notifyCandidateStatus;
    }

    public static NotifyCandidateStatus notifyCandidateStatusOnline(CandidateInfo candidateInfo) {
        NotifyCandidateStatus notifyCandidateStatus = new NotifyCandidateStatus();
        notifyCandidateStatus.setCandidateId(candidateInfo.getId());
        notifyCandidateStatus.setNumberId(candidateInfo.getNumberId());
        notifyCandidateStatus.setCandidateStatus(CandidateStatus.ONLINE);
        return notifyCandidateStatus;
    }
}
