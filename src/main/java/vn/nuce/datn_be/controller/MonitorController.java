package vn.nuce.datn_be.controller;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import vn.nuce.datn_be.enity.*;
import vn.nuce.datn_be.model.dto.*;
import vn.nuce.datn_be.model.dto.ResponseBody;
import vn.nuce.datn_be.model.enumeration.CandidateStatus;
import vn.nuce.datn_be.model.form.RoomSearchForm;
import vn.nuce.datn_be.security.UserDetailsImpl;
import vn.nuce.datn_be.services.*;
import vn.nuce.datn_be.utils.DatnUtils;
import vn.nuce.datn_be.utils.ExcelUtils;

import javax.validation.Valid;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    private static final String REGEX_EMAIL = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\\\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\\\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    @Value("${datn.google.rootFolder.id}")
    private String ROOT_FOLDER_ID;

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
    public ResponseEntity<?> postCreateRoomDefault(@Valid @ModelAttribute RoomForm roomForm) {
        try {
            List<List<String>> rowDataList = null;
            if (roomForm.getFile() != null && roomForm.getFile().getSize() > 0) {
                if (!Objects.requireNonNull(roomForm.getFile().getOriginalFilename()).contains("xlsx") || !roomForm.getFile().getOriginalFilename().contains("xls")) {
                    return new ResponseEntity<>(ResponseBody.responseBodyFail("File invalid"), HttpStatus.OK);
                }
                Workbook workbook;

                boolean xlsx = Objects.requireNonNull(roomForm.getFile().getOriginalFilename()).contains("xlsx");
                if (xlsx) {
                    workbook = new XSSFWorkbook(roomForm.getFile().getInputStream());
                } else {
                    workbook = new HSSFWorkbook(roomForm.getFile().getInputStream());
                }
                rowDataList = ExcelUtils.readUnicodeFile(workbook);

                Set<Long> listNumberId = new HashSet<>();
                Set<String> listEmail = new HashSet<>();

                for (int i = 1; i < rowDataList.size(); i++) {
                    String email = rowDataList.get(i).get(1);
                    if (!email.matches(REGEX_EMAIL)) {
                        return new ResponseEntity<>(ResponseBody.responseBodyFail("Candidate has email invalid"), HttpStatus.OK);
                    }
                    Long numberId = Long.valueOf(rowDataList.get(i).get(3));
                    if (listEmail.contains(email)) {
                        return new ResponseEntity<>(ResponseBody.responseBodyFail("Email of candidate is duplicate"), HttpStatus.OK);
                    } else {
                        listEmail.add(email);
                    }
                    if (listNumberId.contains(numberId)) {
                        return new ResponseEntity<>(ResponseBody.responseBodyFail("Number id of candidate is duplicate"), HttpStatus.OK);
                    } else {
                        listNumberId.add(numberId);
                    }
                }
            }
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date startTime = DatnUtils.getTimeSpecifyMinute(dateFormat.parse(roomForm.getStartDate() + " " + roomForm.getStartTime()));
            Date endTime = DatnUtils.getTimeSpecifyMinute(dateFormat.parse(roomForm.getEndDate() + " " + roomForm.getEndTime()));
            if (startTime.after(endTime)) {
                return new ResponseEntity<>(ResponseBody.responseBodyFail("End time not to be before start time"), HttpStatus.OK);
            }
            Room room = roomService.saveByRoomForm(roomForm, monitorInfoBase().getMonitorId());

            if (room != null) {
                try {
                    driveManager.findOrCreateFolder(ROOT_FOLDER_ID, room.getId().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
                if (rowDataList != null) {
                    for (int i = 1; i < rowDataList.size(); i++) {
                        CandidateInfo info = new CandidateInfo();
                        info.setInfo(rowDataList.get(i).get(2));
                        info.setNumberId(Long.valueOf(rowDataList.get(i).get(3)));
                        info.setCandidateName(rowDataList.get(i).get(0));
                        info.setRoomFk(room.getId());
                        info.setEmail(rowDataList.get(i).get(1));
                        info.setPassword(DatnUtils.randomPassword(7));
                        candidateService.save(info);
                    }
                }
            }

            return room != null
                    ? new ResponseEntity<>(ResponseBody.responseBodySuccess(new DetailsRoom(room)), HttpStatus.OK)
                    : new ResponseEntity<>(ResponseBody.responseBodyFail("One or more apps not supported"), HttpStatus.OK);
        } catch (ParseException e) {
            e.printStackTrace();
            return new ResponseEntity<>(ResponseBody.responseBodyFail("Cannot parse date time, may be wrong format"), HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(ResponseBody.responseBodyFail("Cannot read file"), HttpStatus.OK);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return new ResponseEntity<>(ResponseBody.responseBodyFail("Number Id must is number"), HttpStatus.OK);
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
    public ResponseEntity<?> searchRoom(@RequestParam(name = "key") String key) {
        List<Room> roomList = roomService.searchRoom(key, monitorInfoBase().getMonitorId());
        List<RoomTitleToSearch> roomTitleToSearches = new LinkedList<>();
        roomList.forEach(room -> roomTitleToSearches.add(new RoomTitleToSearch(room)));
        return new ResponseEntity<>(ResponseBody.responseBodySuccess(roomTitleToSearches), HttpStatus.OK);
    }

    @GetMapping("/app/list-app-supported")
    public ResponseEntity<?> listAppsSupported() {
        List<String> appsName = new LinkedList<>();
        appService.findAll().forEach(app -> appsName.add(app.getAppName()));
        return new ResponseEntity<>(ResponseBody.responseBodySuccess(appsName), HttpStatus.OK);
    }

    @GetMapping("/room/search-room-by-form")
    public ResponseEntity<?> searchRoomBySearchForm(@RequestBody RoomSearchForm searchForm) {
        try {
            searchForm.setMonitorId(monitorInfoBase().getMonitorId());
            List<Room> roomList = roomService.findBySearchForm(searchForm);
            List<DetailsRoom> detailsRooms = new LinkedList<>();
            roomList.forEach(room -> detailsRooms.add(new DetailsRoom(room)));
            return new ResponseEntity<>(ResponseBody.responseBodySuccess(detailsRooms), HttpStatus.OK);
        } catch (ParseException e) {
            e.printStackTrace();
            return new ResponseEntity<>(ResponseBody.responseBodyFail("Cannot parse date time, may be wrong format"), HttpStatus.OK);
        }

    }
}
