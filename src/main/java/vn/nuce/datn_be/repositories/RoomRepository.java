package vn.nuce.datn_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.nuce.datn_be.enity.Room;
import vn.nuce.datn_be.model.enumeration.RoomStatus;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByOwnerFk(Long userId);

    List<Room> findAllByStartTimeLessThanEqualAndRoomStatusAndEndTimeGreaterThanEqual(Date startTime, RoomStatus roomStatus, Date endTime);

    List<Room> findAllByEndTimeLessThanEqualAndRoomStatus(Date endTime, RoomStatus roomStatus);

    List<Room> findAllDistinctByIdOrNameStartingWithAndOwnerFk(Long id, String name, Long ownerFk);

    List<Room> findAllByStartTimeGreaterThanEqualAndStartTimeLessThanEqual(Date startDate, Date endDate);

    List<Room> findAllByRoomStatus(RoomStatus roomStatus);

    boolean existsByIdAndOwnerFk(Long roomId, Long monitorId);
}
