package vn.nuce.datn_be.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.nuce.datn_be.enity.CandidateInfo;
import vn.nuce.datn_be.enity.Room;
import vn.nuce.datn_be.model.dto.DetailsRoom;
import vn.nuce.datn_be.model.dto.ResponseBody;
import vn.nuce.datn_be.security.UserDetailsImpl;
import vn.nuce.datn_be.services.CandidateService;
import vn.nuce.datn_be.services.RoomService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/candidate")
public class CandidateController {
    @Autowired
    RoomService roomService;

    @Autowired
    CandidateService candidateService;

    private UserDetailsImpl candidateInfoBase() {
        return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @GetMapping("/details-room")
    public ResponseEntity<?> getDetailsRoomById() {
        CandidateInfo candidateInfo= candidateService.findById(candidateInfoBase().getCandidateId());
        if (candidateInfo!=null) {
            Room room = candidateInfo.getRoom();
            return room != null
                    ? new ResponseEntity<>(ResponseBody.responseBodySuccess(new DetailsRoom(room)), HttpStatus.OK)
                    : new ResponseEntity<>(ResponseBody.responseBodyFail("RoomId not found"), HttpStatus.OK);
        }
        return new ResponseEntity<>(ResponseBody.responseBodyFail("Candidate not found"), HttpStatus.OK);
    }
}
