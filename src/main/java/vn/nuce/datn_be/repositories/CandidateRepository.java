package vn.nuce.datn_be.repositories;

import org.hibernate.annotations.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.nuce.datn_be.enity.CandidateInfo;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface CandidateRepository extends JpaRepository<CandidateInfo, String> {
    Optional<CandidateInfo> findByNumberId(Long numberId);

    List<CandidateInfo> findAllByRoomFk(Long roomId);

    void deleteAllByRoomFk(Long roomId);

    @Modifying
    @Query(value = "update CandidateInfo set candidateStatus='ONLINE' where id =:id")
    void updateCandidateStatusOnlineById(@Param("id") String id);

    @Modifying
    @Query(value = "update CandidateInfo set lastSaw=:lastSaw where id =:id")
    void updateLastSawById(@Param("id") String id, @Param("lastSaw") Date lastSaw);

    @Modifying
    @Query(value = "update CandidateInfo set isBlocked=:state where id =:id")
    void blockCandidate(@Param("state") boolean state,@Param("id") String id);
}
