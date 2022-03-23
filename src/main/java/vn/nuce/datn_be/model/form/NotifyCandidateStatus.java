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

    boolean isLogin;

    boolean isLogout;

    public static NotifyCandidateStatus notifyCandidateStatusDisconnected(CandidateInfo candidateInfo, boolean isLogout) {
        NotifyCandidateStatus notifyCandidateStatus = new NotifyCandidateStatus();
        notifyCandidateStatus.setCandidateId(candidateInfo.getId());
        notifyCandidateStatus.setNumberId(candidateInfo.getNumberId());
        notifyCandidateStatus.setCandidateStatus(CandidateStatus.DISCONNECTED);
        notifyCandidateStatus.setLogout(isLogout);
        notifyCandidateStatus.setLogin(false);
        return notifyCandidateStatus;
    }

    public static NotifyCandidateStatus notifyCandidateStatusOnline(CandidateInfo candidateInfo, boolean isLogin) {
        NotifyCandidateStatus notifyCandidateStatus = new NotifyCandidateStatus();
        notifyCandidateStatus.setCandidateId(candidateInfo.getId());
        notifyCandidateStatus.setNumberId(candidateInfo.getNumberId());
        notifyCandidateStatus.setCandidateStatus(CandidateStatus.ONLINE);
        notifyCandidateStatus.setLogin(isLogin);
        notifyCandidateStatus.setLogout(false);
        return notifyCandidateStatus;
    }
}
