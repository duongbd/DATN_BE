package vn.nuce.datn_be.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nuce.datn_be.enity.App;
import vn.nuce.datn_be.enity.Room;
import vn.nuce.datn_be.enity.RoomAppKey;
import vn.nuce.datn_be.model.dto.ResponseBody;
import vn.nuce.datn_be.model.dto.RoomForm;
import vn.nuce.datn_be.model.enumeration.RoomStatus;
import vn.nuce.datn_be.repositories.AppRepository;
import vn.nuce.datn_be.repositories.RoomAppKeyRepository;
import vn.nuce.datn_be.repositories.RoomRepository;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

@Service
@Transactional
public class RoomService {
    @Autowired
    RoomRepository roomRepository;

    @Autowired
    AppRepository appRepository;

    @Autowired
    RoomAppKeyRepository roomAppKeyRepository;

    public List<Room> getListRoomOwnerById(Long userId) {
        return roomRepository.findByUserFk(userId);
    }

    public Room findById(Long roomId) {
        return roomRepository.findById(roomId).orElse(null);
    }

    public boolean existById(Long roomId) {
        return roomRepository.existsById(roomId);
    }

    public Room save(Room room) {
        return roomRepository.save(room);
    }

    public Room saveByRoomForm(RoomForm roomForm, Long monitorId) throws ParseException {
        Room room = new Room();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        room.setStartTime(dateFormat.parse(roomForm.getStartDate() + " " + roomForm.getStartTime()));
        room.setEndTime(dateFormat.parse(roomForm.getEndDate() + " " + roomForm.getEndTime()));
        room.setName(roomForm.getName());
        room.setRoomStatus(RoomStatus.INACTIVE);
        room.setUserFk(monitorId);
        room=roomRepository.save(room);
        if (!roomForm.getApps().isEmpty()) {
            List<App> apps = appRepository.findAllByAppNameIn(roomForm.getApps());
            if (Integer.valueOf(apps.size()).equals(roomForm.getApps().size())) {
                Room finalRoom = room;
                apps.forEach(app -> {
                    RoomAppKey roomAppKey = new RoomAppKey();
                    roomAppKey.setRoomFk(finalRoom.getId());
                    roomAppKey.setAppFk(app.getId());
                    roomAppKey.setRoom(finalRoom);
                    roomAppKey.setApp(app);
                    roomAppKey = roomAppKeyRepository.save(roomAppKey);
                    if (finalRoom.getRoomAppKeys() == null) {
                        List<RoomAppKey> roomAppKeys = new LinkedList<>();
                        finalRoom.setRoomAppKeys(roomAppKeys);
                    }
                    finalRoom.getRoomAppKeys().add(roomAppKey);
                });
//                roomRepository.save(finalRoom);
            }
            else
            return null;
        }
        return room;
    }
}
