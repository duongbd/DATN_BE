package vn.nuce.datn_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.nuce.datn_be.enity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    void deleteAllByRoomFk(Long roomId);
}
