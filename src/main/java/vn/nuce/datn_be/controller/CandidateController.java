package vn.nuce.datn_be.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.nuce.datn_be.enity.CandidateInfo;
import vn.nuce.datn_be.enity.LogTime;
import vn.nuce.datn_be.enity.Room;
import vn.nuce.datn_be.model.dto.DetailsRoom;
import vn.nuce.datn_be.model.dto.ResponseBody;
import vn.nuce.datn_be.model.enumeration.MonitoringStatus;
import vn.nuce.datn_be.model.form.MonitoringInfo;
import vn.nuce.datn_be.model.form.NotifyCandidateStatus;
import vn.nuce.datn_be.repositories.LogTimeRepository;
import vn.nuce.datn_be.security.UserDetailsImpl;
import vn.nuce.datn_be.services.CandidateService;
import vn.nuce.datn_be.services.GoogleDriveManager;
import vn.nuce.datn_be.services.LogTimeService;
import vn.nuce.datn_be.services.RoomService;
import vn.nuce.datn_be.utils.DatnUtils;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Date;

@Log4j2
@CrossOrigin(origins = "http://localhost:8080", maxAge = 3600)
@RestController
@RequestMapping("/candidate")
public class CandidateController {
    @Autowired
    RoomService roomService;

    @Autowired
    CandidateService candidateService;

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private LogTimeService logTimeService;

    @Autowired
    private GoogleDriveManager driveManager;

    private UserDetailsImpl candidateInfoBase() {
        return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @GetMapping("/details-room")
    public ResponseEntity<?> getDetailsRoomById() {
        CandidateInfo candidateInfo = candidateService.findById(candidateInfoBase().getCandidateId());
        if (candidateInfo != null) {
            Room room = candidateInfo.getRoom();
            return room != null
                    ? new ResponseEntity<>(ResponseBody.responseBodySuccess(new DetailsRoom(room)), HttpStatus.OK)
                    : new ResponseEntity<>(ResponseBody.responseBodyFail("RoomId not found"), HttpStatus.OK);
        }
        return new ResponseEntity<>(ResponseBody.responseBodyFail("Candidate not found"), HttpStatus.OK);
    }

    @PostMapping("/update-info")
    public ResponseEntity<?> postUpdateMonitoringCandidate(@Valid @ModelAttribute MonitoringInfo monitoringInfo) {
        log.info("update");
        CandidateInfo candidateInfo = candidateService.findById(candidateInfoBase().getCandidateId());
        //update to log table
        LogTime logTime = new LogTime();
        logTime.setTimeCreate(new Date());
        logTime.setRoomFk(candidateInfo.getRoomFk());
        switch (monitoringInfo.getMonitoringStatus()) {
            case NORMAL:
                logTime.setContent("CandidateId: " + candidateInfo.getId() + " - " + "numberId: " + candidateInfo.getNumberId() + " still normal");
                break;
            case WARN:
            case ALERT:
                logTime.setContent("CandidateId: " + candidateInfo.getId() + " - " + "numberId: " + candidateInfo.getNumberId() + " has " + monitoringInfo.getMonitoringStatus() + ": " + monitoringInfo.getViolationError() + " with proof - " + monitoringInfo.getViolationInfo());
                Runnable runnableAlert = () -> {
                    template.convertAndSend("/notify/monitor/" + candidateInfo.getRoomFk(), monitoringInfo);
                };
                runnableAlert.run();
                break;
        }
        Runnable uploadScreenShotToDrive = () -> {
            try {
                candidateInfo.setNewestScreenShotId(driveManager.uploadFile(monitoringInfo.getFile(), "DATN/" + candidateInfo.getRoomFk() + "/" + candidateInfo.getId()));
                candidateInfo.setLastSaw(DatnUtils.cvtToGmt(new Date(),7));
                candidateService.save(candidateInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        uploadScreenShotToDrive.run();
        logTimeService.save(logTime);
        return new ResponseEntity<>(ResponseBody.responseBodySuccess(null), HttpStatus.OK);
    }
}
