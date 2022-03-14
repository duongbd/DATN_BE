package vn.nuce.datn_be.controller;

import com.google.api.services.drive.model.File;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import vn.nuce.datn_be.enity.CandidateInfo;
import vn.nuce.datn_be.enity.Room;
import vn.nuce.datn_be.enity.User;
import vn.nuce.datn_be.model.dto.CandidateLoginForm;
import vn.nuce.datn_be.model.dto.LoginSuccessDto;
import vn.nuce.datn_be.model.dto.MonitorLoginForm;
import vn.nuce.datn_be.model.dto.ResponseBody;
import vn.nuce.datn_be.model.enumeration.CandidateStatus;
import vn.nuce.datn_be.model.enumeration.RoomStatus;
import vn.nuce.datn_be.model.form.NotifyCandidateStatus;
import vn.nuce.datn_be.repositories.RoomRepository;
import vn.nuce.datn_be.services.CandidateService;
import vn.nuce.datn_be.services.GoogleDriveManager;
import vn.nuce.datn_be.services.RoomService;
import vn.nuce.datn_be.services.UserService;
import vn.nuce.datn_be.utils.DatnUtils;
import vn.nuce.datn_be.utils.ExcelUtils;
import vn.nuce.datn_be.utils.JwtUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8080", maxAge = 3600)
@RestController
@RequestMapping("/auth")
@Log4j2
public class AuthenticationController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserService userService;
    @Autowired
    CandidateService candidateService;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    RoomService roomService;
    @Autowired
    private SimpMessagingTemplate template;

    @Value("${datn.google.rootFolder.id}")
    private String ROOT_FOLDER_ID;

    private String authenticate(String username, String password) throws Exception {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtils.generateJwtToken(authentication);
    }

    @PostMapping(value = "/monitor/login")
    public ResponseEntity<?> loginMonitor(@RequestBody MonitorLoginForm monitorLoginForm) {
        try {
            String token = authenticate(monitorLoginForm.getEmail(), monitorLoginForm.getPassword());
            return new ResponseEntity<>(ResponseBody.responseBodySuccess(new LoginSuccessDto(token)), HttpStatus.OK);
        } catch (Exception exception) {
            log.info(exception.getMessage());
            return new ResponseEntity<>(ResponseBody.responseBodyFail("Wrong username or password"), HttpStatus.OK);
        }
    }

    @PostMapping(value = "/candidate/login")
    public ResponseEntity<?> loginCandidate(@RequestBody CandidateLoginForm candidateLoginForm) {
        try {
            String token = authenticate(candidateLoginForm.getUsername(), candidateLoginForm.getPassword());
            Room room = candidateService.findById(candidateLoginForm.getUsername()).getRoom();
            if (room.getRoomStatus().equals(RoomStatus.ACTIVE)) {
                CandidateInfo candidateInfo = candidateService.findById(candidateLoginForm.getUsername());
                if (candidateInfo.getCandidateStatus().equals(CandidateStatus.BLOCK)){
                    return new ResponseEntity<>(ResponseBody.responseBodyFail("BLOCKED"), HttpStatus.OK);
                }
                candidateInfo.setCandidateStatus(CandidateStatus.ONLINE);
                candidateInfo.setLastSaw(DatnUtils.cvtToGmt(new Date(), 7));
                candidateService.save(candidateInfo);
                this.template.convertAndSend("/chat/notify-status/candidate" + candidateInfo.getRoomFk(), NotifyCandidateStatus.notifyCandidateStatusOnline(candidateInfo));
                return new ResponseEntity<>(ResponseBody.responseBodySuccess(new LoginSuccessDto(token)), HttpStatus.OK);
            }
            return new ResponseEntity<>(ResponseBody.responseBodyFail("Room is not opened"), HttpStatus.OK);
        } catch (Exception exception) {
            log.info(exception.getMessage());
            return new ResponseEntity<>(ResponseBody.responseBodyFail("Wrong username or password"), HttpStatus.OK);
        }
    }

    @PostMapping(value = "/monitor/sign-up")
    public ResponseEntity<?> signUpMonitor(@RequestBody MonitorLoginForm monitorLoginForm) {
        User user = new User();
        user.setUsername(monitorLoginForm.getEmail());
        user.setPassword(monitorLoginForm.getPassword());
        userService.save(user);
        return new ResponseEntity<String>(user.toString(), HttpStatus.OK);
    }

    @PostMapping(value = "/candidate/signUp")
    public ResponseEntity<?> signUp(@RequestBody MonitorLoginForm monitorLoginForm) {
        CandidateInfo candidateInfo = new CandidateInfo();
        candidateInfo.setCandidateName(monitorLoginForm.getEmail());
        candidateInfo.setPassword(monitorLoginForm.getPassword());
        candidateInfo.setCandidateStatus(CandidateStatus.ONLINE);
        candidateService.save(candidateInfo);
        return new ResponseEntity<String>(candidateInfo.toString(), HttpStatus.OK);
    }

    @PostMapping(value = "/logout")
    public ResponseEntity<?> logoutMonitor() {
        SecurityContextHolder.clearContext();
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    GoogleDriveManager googleDriveManager;

    @GetMapping(value = "/gg")
    public ResponseEntity<?> auto(){
        List<Room> idList= roomRepository.findAll();
        for (Room room : idList) {
            try {
                log.info(googleDriveManager.findOrCreateFolder(ROOT_FOLDER_ID, room.getId().toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        List<File> files = null;
//        try {
//            files = googleDriveManager.googleDriveService().files().list().execute().getFiles();
//            files.forEach(file -> {
//                Runnable runnable = () -> {
//                    try {
//                        if (!file.getName().equals("DATN"))
//                            googleDriveManager.deleteFile(file.getId());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                };
//                runnable.run();
//            });
//        } catch (IOException | GeneralSecurityException e) {
//            e.printStackTrace();
//        }
//        if (files == null || files.isEmpty()) {
//            System.out.println("No files found.");
//        } else {
//            System.out.println("Files:");
//            for (File file : files) {
//                System.out.printf("%s (%s)\n", file.getName(), file.getId());
//            }
//        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
