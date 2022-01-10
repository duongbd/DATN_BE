package vn.nuce.datn_be.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.nuce.datn_be.enity.Room;
import vn.nuce.datn_be.repositories.RoomRepository;

import java.util.List;

@Service
public class RoomService {
    @Autowired
    RoomRepository roomRepository;

    public List<Room> getListRoomOwnerById(Long userId){
        return roomRepository.findByUserFk(userId);
    }
}
