package vn.nuce.datn_be.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.nuce.datn_be.enity.CandidateInfo;
import vn.nuce.datn_be.enity.Room;
import vn.nuce.datn_be.enity.User;
import vn.nuce.datn_be.model.dto.DetailsRoom;
import vn.nuce.datn_be.model.dto.ResponseBody;
import vn.nuce.datn_be.model.dto.RoomForm;
import vn.nuce.datn_be.model.enumeration.RoomStatus;
import vn.nuce.datn_be.security.UserDetailsImpl;
import vn.nuce.datn_be.services.CandidateService;
import vn.nuce.datn_be.services.RoomService;
import vn.nuce.datn_be.services.UserService;

import javax.validation.Valid;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@CrossOrigin(origins = "http://35.173.233.67/", maxAge = 3600)
@RestController
@RequestMapping("/monitor")
public class MonitorController {
    @Autowired
    UserService userService;

    @Autowired
    RoomService roomService;

    @Autowired
    CandidateService candidateService;

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
        return room != null && room.getUserFk().equals(monitorInfoBase().getMonitorId())
                ? new ResponseEntity<>(ResponseBody.responseBodySuccess(new DetailsRoom(room)), HttpStatus.OK)
                : new ResponseEntity<>(ResponseBody.responseBodyFail("Room not found"), HttpStatus.OK);
    }

    @PostMapping("/room/create")
    public ResponseEntity<?> postCreateRoomDefault(@Valid @RequestBody RoomForm roomForm) {
        Room room = new Room();
        room.setStartTime(roomForm.getStartTime());
        room.setEndTime(roomForm.getEndTime());
        room.setUrls(roomForm.getUrls());
        room.setName(roomForm.getName());
        room.setUserFk(monitorInfoBase().getMonitorId());
        room.setRoomStatus(RoomStatus.INACTIVE);
        return new ResponseEntity<>(new DetailsRoom(roomService.save(room)), HttpStatus.OK);
    }

    @GetMapping("/list-candidate")
    public ResponseEntity<?> getListCandidateByRoomId(@RequestParam(name = "roomId") Long roomId) {
        if (roomService.existById(roomId)) {
            List<CandidateInfo> candidateInfoList = candidateService.findAllCandidateByRoomId(roomId);
            return new ResponseEntity<>(ResponseBody.responseBodySuccess(candidateInfoList), HttpStatus.OK);
        }
        return new ResponseEntity<>(ResponseBody.responseBodyFail("roomId not exists"), HttpStatus.OK);
    }
}
