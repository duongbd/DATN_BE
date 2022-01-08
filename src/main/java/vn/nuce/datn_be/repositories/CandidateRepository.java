package vn.nuce.datn_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.nuce.datn_be.enity.CandidateInfo;

import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<CandidateInfo, String> {
    Optional<CandidateInfo> findByNumberId(Long numberId);
}
