package vn.nuce.datn_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.nuce.datn_be.enity.Room;
import vn.nuce.datn_be.model.enumeration.RoomStatus;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByOwnerFk(Long userId);

    boolean existsById(Long roomId);

    List<Room> findAllByStartTimeLessThanEqualAndRoomStatusAndEndTimeGreaterThan(Date startTime, RoomStatus roomStatus, Date endTime);

    List<Room> findAllByEndTimeLessThanEqualAndRoomStatus(Date endTime, RoomStatus roomStatus);
}
