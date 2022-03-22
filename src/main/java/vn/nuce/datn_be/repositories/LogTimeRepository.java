package vn.nuce.datn_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.nuce.datn_be.enity.LogTime;

import java.util.List;

@Repository
public interface LogTimeRepository extends JpaRepository<LogTime, Long> {
    List<LogTime> findAllByRoomFk(Long roomId);
    void deleteAllByRoomFk(Long roomId);
}
