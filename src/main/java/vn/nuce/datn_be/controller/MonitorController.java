package vn.nuce.datn_be.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.nuce.datn_be.enity.*;
import vn.nuce.datn_be.model.dto.*;
import vn.nuce.datn_be.model.dto.ResponseBody;
import vn.nuce.datn_be.model.enumeration.CandidateStatus;
import vn.nuce.datn_be.security.UserDetailsImpl;
import vn.nuce.datn_be.services.*;
import vn.nuce.datn_be.utils.DatnUtils;

import javax.validation.Valid;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8080", maxAge = 3600)
@RestController
@RequestMapping("/monitor")
public class MonitorController {
    @Autowired
    UserService userService;

    @Autowired
    RoomService roomService;

    @Autowired
    CandidateService candidateService;

    @Autowired
    ConvertService convertService;

    @Autowired
    AppService appService;

    @Autowired
    GoogleDriveManager driveManager;

    private UserDetailsImpl monitorInfoBase() {
        return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @GetMapping("/list-owner-room")
    public ResponseEntity<?> getListOwnerRoom() {
        List<Room> roomOwnerById = roomService.getListRoomOwnerById(monitorInfoBase().getMonitorId());
        List<DetailsRoom> detailsRooms = new LinkedList<>();
        roomOwnerById.forEach(room -> detailsRooms.add(new DetailsRoom(room)));
        return new ResponseEntity<>(ResponseBody.responseBodySuccess(detailsRooms), HttpStatus.OK);
    }

    @GetMapping("/details-room")
    public ResponseEntity<?> getDetailsRoomById(@RequestParam(name = "id") Long roomId) {
        Room room = roomService.findById(roomId);
        return room != null && room.getOwnerFk().equals(monitorInfoBase().getMonitorId())
                ? new ResponseEntity<>(ResponseBody.responseBodySuccess(new DetailsRoom(room)), HttpStatus.OK)
                : new ResponseEntity<>(ResponseBody.responseBodyFail("Room not found"), HttpStatus.OK);
    }

    @PostMapping("/room/create")
    public ResponseEntity<?> postCreateRoomDefault(@Valid @RequestBody RoomForm roomForm) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date startTime = DatnUtils.getTimeSpecifyMinute(dateFormat.parse(roomForm.getStartDate() + " " + roomForm.getStartTime()));
            Date endTime = DatnUtils.getTimeSpecifyMinute(dateFormat.parse(roomForm.getEndDate() + " " + roomForm.getEndTime()));
            if (startTime.after(endTime)) {
                return new ResponseEntity<>(ResponseBody.responseBodyFail("End time not to be before start time"), HttpStatus.OK);
            }
            Room room = roomService.saveByRoomForm(roomForm, monitorInfoBase().getMonitorId());

            return room != null
                    ? new ResponseEntity<>(ResponseBody.responseBodySuccess(new DetailsRoom(room)), HttpStatus.OK)
                    : new ResponseEntity<>(ResponseBody.responseBodyFail("One or more apps not supported"), HttpStatus.OK);
        } catch (ParseException e) {
            e.printStackTrace();
            return new ResponseEntity<>(ResponseBody.responseBodyFail("Cannot parse date time, may be wrong format"), HttpStatus.OK);
        }
    }

    @GetMapping("/list-candidate")
    public ResponseEntity<?> getListCandidateByRoomId(@RequestParam(name = "roomId") Long roomId) {
        if (roomService.existById(roomId)) {
            List<CandidateInfo> candidateInfoList = candidateService.findAllCandidateByRoomId(roomId);
            return new ResponseEntity<>(ResponseBody.responseBodySuccess(candidateInfoList), HttpStatus.OK);
        }
        return new ResponseEntity<>(ResponseBody.responseBodyFail("roomId not exists"), HttpStatus.OK);
    }

    @GetMapping("/list-screen-shot-candidate-id-by-room")
    public ResponseEntity<?> getListUrlScreenShotNewest(@RequestParam(name = "roomId") Long roomId) {
        if (roomService.existById(roomId)) {
            List<CandidateInfo> candidateInfoList = candidateService.findAllCandidateByRoomId(roomId);
            List<CandidateUrlScreenShotForm> candidateUrlScreenShotForms = new LinkedList<>();
            candidateInfoList.forEach(candidateInfo -> {
                CandidateUrlScreenShotForm form = new CandidateUrlScreenShotForm();
                form.setStatus(candidateInfo.getCandidateStatus());
                form.setCandidateId(candidateInfo.getId());
                form.setNumberId(candidateInfo.getNumberId());
                if (candidateInfo.getCandidateStatus().equals(CandidateStatus.ONLINE) && candidateInfo.getNewestScreenShotId() != null) {
                    form.setFileId(candidateInfo.getNewestScreenShotId());
                }
                candidateUrlScreenShotForms.add(form);
            });
            return new ResponseEntity<>(ResponseBody.responseBodySuccess(candidateUrlScreenShotForms), HttpStatus.OK);
        }
        return new ResponseEntity<>(ResponseBody.responseBodyFail("roomId not exists"), HttpStatus.OK);
    }

    @GetMapping("/room/search")
    public ResponseEntity<?> searchRoom(@RequestParam(name = "key") String key){
        List<Room> roomList = roomService.searchRoom(key, monitorInfoBase().getMonitorId());
        List<RoomTitleToSearch> roomTitleToSearches = new LinkedList<>();
        roomList.forEach(room -> roomTitleToSearches.add(new RoomTitleToSearch(room)));
        return new ResponseEntity<>(ResponseBody.responseBodySuccess(roomTitleToSearches), HttpStatus.OK);
    }
}
