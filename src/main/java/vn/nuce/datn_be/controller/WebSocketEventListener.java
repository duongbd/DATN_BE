package vn.nuce.datn_be.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;
import vn.nuce.datn_be.enity.CandidateInfo;
import vn.nuce.datn_be.model.enumeration.CandidateStatus;
import vn.nuce.datn_be.model.form.NotifyCandidateStatus;
import vn.nuce.datn_be.security.UserDetailsImpl;
import vn.nuce.datn_be.services.CandidateService;
import vn.nuce.datn_be.utils.DatnUtils;

@Controller
@Component
public class WebSocketEventListener {
    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private CandidateService candidateService;

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        if (event.getUser() != null) {
            UserDetailsImpl userDetails = DatnUtils.principalToUser(event.getUser());
            if (userDetails.getCandidateId() != null) {
                CandidateInfo candidateInfo = this.candidateService.findById(userDetails.getCandidateId());
                NotifyCandidateStatus status = new NotifyCandidateStatus();
                status.setCandidateId(userDetails.getCandidateId());
                status.setNumberId(userDetails.getCandidateNumberId());
                status.setCandidateStatus(CandidateStatus.DISCONNECTED);
                candidateInfo.setCandidateStatus(CandidateStatus.DISCONNECTED);
                this.candidateService.save(candidateInfo);
                this.template.convertAndSend("/chat/notify-status/" + candidateInfo.getRoomFk(), status);
            }
        }
    }

    @EventListener
    public void handleSessionDisconnect(SessionUnsubscribeEvent event) {
        if (event.getUser() != null) {
            UserDetailsImpl userDetails = DatnUtils.principalToUser(event.getUser());
            if (userDetails.getCandidateId() != null) {
                CandidateInfo candidateInfo = this.candidateService.findById(userDetails.getCandidateId());
                NotifyCandidateStatus status = new NotifyCandidateStatus();
                status.setCandidateId(userDetails.getCandidateId());
                status.setNumberId(userDetails.getCandidateNumberId());
                status.setCandidateStatus(CandidateStatus.DISCONNECTED);
                candidateInfo.setCandidateStatus(CandidateStatus.DISCONNECTED);
                this.candidateService.save(candidateInfo);
                this.template.convertAndSend("/chat/notify-status/" + candidateInfo.getRoomFk(), status);
            }
        }
    }

    @EventListener
    public void handleSessionConnected(SessionSubscribeEvent event) {
        SecurityContextHolder.getContext().getAuthentication();
        if (event.getUser() != null) {
            UserDetailsImpl userDetails = DatnUtils.principalToUser(event.getUser());
            if (userDetails.getCandidateId() != null) {
                CandidateInfo candidateInfo = this.candidateService.findById(userDetails.getCandidateId());
                NotifyCandidateStatus status = new NotifyCandidateStatus();
                status.setCandidateId(userDetails.getCandidateId());
                status.setNumberId(userDetails.getCandidateNumberId());
                status.setCandidateStatus(CandidateStatus.ONLINE);
                candidateInfo.setCandidateStatus(CandidateStatus.ONLINE);
                this.candidateService.save(candidateInfo);
                this.template.convertAndSend("/chat/notify-status/" + candidateInfo.getRoomFk(), status);
            }
        }
    }

    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event) {
        SecurityContextHolder.getContext().getAuthentication();
        if (event.getUser() != null) {
            UserDetailsImpl userDetails = DatnUtils.principalToUser(event.getUser());
            if (userDetails.getCandidateId() != null) {
                CandidateInfo candidateInfo = this.candidateService.findById(userDetails.getCandidateId());
                NotifyCandidateStatus status = new NotifyCandidateStatus();
                status.setCandidateId(userDetails.getCandidateId());
                status.setNumberId(userDetails.getCandidateNumberId());
                status.setCandidateStatus(CandidateStatus.ONLINE);
                candidateInfo.setCandidateStatus(CandidateStatus.ONLINE);
                this.candidateService.save(candidateInfo);
                this.template.convertAndSend("/chat/notify-status/" + candidateInfo.getRoomFk(), status);
            }
        }
    }
}
