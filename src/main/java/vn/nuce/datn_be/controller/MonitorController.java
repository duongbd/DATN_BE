package vn.nuce.datn_be.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.nuce.datn_be.enity.*;
import vn.nuce.datn_be.model.dto.DetailsRoom;
import vn.nuce.datn_be.model.dto.ResponseBody;
import vn.nuce.datn_be.model.dto.RoomForm;
import vn.nuce.datn_be.security.UserDetailsImpl;
import vn.nuce.datn_be.services.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
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
}
