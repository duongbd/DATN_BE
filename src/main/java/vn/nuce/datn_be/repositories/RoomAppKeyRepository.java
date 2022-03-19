package vn.nuce.datn_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.nuce.datn_be.enity.RoomAppKey;

import java.util.List;

@Repository
public interface RoomAppKeyRepository extends JpaRepository<RoomAppKey, Long> {
    void deleteAllByRoomFk(Long roomId);
}
