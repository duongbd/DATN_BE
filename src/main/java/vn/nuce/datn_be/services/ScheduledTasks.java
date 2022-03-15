package vn.nuce.datn_be.services;


import lombok.extern.log4j.Log4j2;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import vn.nuce.datn_be.enity.CandidateInfo;
import vn.nuce.datn_be.enity.LogTime;
import vn.nuce.datn_be.enity.Room;
import vn.nuce.datn_be.model.enumeration.CandidateStatus;
import vn.nuce.datn_be.model.enumeration.RoomStatus;
import vn.nuce.datn_be.model.enumeration.SendMailStatus;
import vn.nuce.datn_be.model.form.NotifyCandidateStatus;
import vn.nuce.datn_be.model.form.NotifyRoomStatus;
import vn.nuce.datn_be.utils.DatnUtils;

import javax.mail.MessagingException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Log4j2
public class ScheduledTasks {

    @Autowired
    RoomService roomService;

    @Autowired
    LogTimeService logTimeService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private SimpMessagingTemplate template;

    @Scheduled(fixedRate = 60000)
    public void startRoomWhenTimeOn() throws InterruptedException {
        Date start = new Date();
        log.info("Before sleep: " + start);
        Calendar startCalendar = Calendar.getInstance();
        Calendar desCalendar = Calendar.getInstance();
        Calendar nowCalendar = Calendar.getInstance();
        startCalendar.setTime(start);
        desCalendar.setTime(new Date());
        desCalendar.set(Calendar.SECOND, 0);
        desCalendar.add(Calendar.MINUTE, 5);
        nowCalendar.add(Calendar.SECOND, 60 - nowCalendar.get(Calendar.SECOND));
        List<Room> rooms = roomService.getAllRoomTimeOn(DatnUtils.cvtToGmt(desCalendar.getTime(), 7));
        TimeUnit.MILLISECONDS.sleep(nowCalendar.getTime().getTime() - new Date().getTime());
        rooms.forEach(room -> {
            if (room.getStartTime().before(DatnUtils.cvtToGmt(new Date(), 7)) && room.getEndTime().after(DatnUtils.cvtToGmt(new Date(), 7))) {
                room.setRoomStatus(RoomStatus.ACTIVE);
                roomService.save(room);
                this.template.convertAndSend("/chat/notify-status/room/" + room.getId(), NotifyRoomStatus.notifyRoomStatus(room));
                LogTime logTime = new LogTime();
                logTime.setRoomFk(room.getId());
                logTime.setTimeCreate(DatnUtils.cvtToGmt(new Date(), 7));
                logTime.setContent("Open room");
                logTimeService.save(logTime);
                log.info("Open room with id: " + room.getId());
            }
        });
        log.info("After sleep: " + new Date());
    }

    @Scheduled(fixedRate = 60000)
    public void endRoomWhenTimeOn() throws InterruptedException {
        Date start = new Date();
        log.info("Before sleep: " + start);
        Calendar startCalendar = Calendar.getInstance();
        Calendar desCalendar = Calendar.getInstance();
        Calendar nowCalendar = Calendar.getInstance();
        startCalendar.setTime(start);
        desCalendar.setTime(new Date());
        desCalendar.set(Calendar.SECOND, 0);
        desCalendar.add(Calendar.MINUTE, 5);
        nowCalendar.add(Calendar.SECOND, 60 - nowCalendar.get(Calendar.SECOND));
        List<Room> rooms = roomService.getAllRoomTimeUp(DatnUtils.cvtToGmt(desCalendar.getTime(), 7));
        TimeUnit.MILLISECONDS.sleep(nowCalendar.getTime().getTime() - new Date().getTime());
        rooms.forEach(room -> {
            if (room.getStartTime().before(DatnUtils.cvtToGmt(new Date(), 7)) && room.getEndTime().before(DatnUtils.cvtToGmt(new Date(), 7))) {
                room.setRoomStatus(RoomStatus.ENDED);
                roomService.save(room);
                this.template.convertAndSend("/chat/notify-status/room/" + room.getId(), NotifyRoomStatus.notifyRoomStatus(room));
                LogTime logTime = new LogTime();
                logTime.setRoomFk(room.getId());
                logTime.setTimeCreate(DatnUtils.cvtToGmt(new Date(), 7));
                logTime.setContent("End room");
                logTimeService.save(logTime);
                log.info("End room with id: " + room.getId());
            }
        });
        log.info("After sleep: " + new Date());
    }

    @Scheduled(fixedRate = 60000)
    public void checkCandidateDisconnected() {
        List<Room> roomList = roomService.getListRoomActive();
        roomList.forEach(room -> {
            Set<CandidateInfo> candidateInfos = room.getCandidateInfos();
            candidateInfos.forEach(candidateInfo -> {
                if (candidateInfo.getLastSaw() != null) {
                    Calendar lastSawAfter6M = Calendar.getInstance();
                    lastSawAfter6M.setTime(candidateInfo.getLastSaw());
                    lastSawAfter6M.add(Calendar.MINUTE, 6);
                    if (DatnUtils.cvtToGmt(new Date(), 7).after(lastSawAfter6M.getTime())){
                        candidateInfo.setCandidateStatus(CandidateStatus.DISCONNECTED);
                        candidateService.save(candidateInfo);
                        this.template.convertAndSend("/chat/notify-status/candidate/" + candidateInfo.getRoomFk(), NotifyCandidateStatus.notifyCandidateStatusDisconnected(candidateInfo));
                    }
                }
            });
        });

    }

    @Scheduled(cron = "0 0 0 ? * *", zone = "GMT+7:00")
    public void autoSendMailToCandidate() {
        List<Room> roomList = roomService.getListRoomNeedSendMailToCandidate();
        roomList.forEach(room -> {
            Set<CandidateInfo> candidateInfos = room.getCandidateInfos();
            candidateInfos.forEach(candidateInfo -> {
                Map<String, Object> map = new ModelMap();
                map.put("id", candidateInfo.getId());
                map.put("key", candidateInfo.getPassword());
                Runnable runnableSendMail = () -> {
                    try {
                        emailService.sendMessageUsingThymeleafTemplate(candidateInfo.getEmail(), "INFO LOGIN ROOM " + candidateInfo.getRoom().getName().toUpperCase(), map);
                        candidateInfo.setSendMailStatus(SendMailStatus.SEND);
                        log.info("Send mail success to candidate id: " + candidateInfo.getId());
                    } catch (MessagingException e) {
//                        e.printStackTrace();
                        log.error(e.getMessage());
                        candidateInfo.setSendMailStatus(SendMailStatus.FAIL);
                        log.info("Send mail fail to candidate id: " + candidateInfo.getId());
                    }
                    candidateService.save(candidateInfo);
                };
                runnableSendMail.run();
            });
        });

    }
}