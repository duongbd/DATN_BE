package vn.nuce.datn_be.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nuce.datn_be.enity.CandidateInfo;
import vn.nuce.datn_be.model.enumeration.CandidateStatus;
import vn.nuce.datn_be.repositories.CandidateRepository;
import vn.nuce.datn_be.utils.DatnUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

@Transactional
@Service
public class CandidateService {
    @Autowired
    CandidateRepository candidateRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public CandidateInfo findByNumberId(Long numberId) {
        return candidateRepository.findByNumberId(numberId).orElse(null);
    }

    public CandidateInfo save(CandidateInfo candidateInfo) {
        return candidateRepository.save(candidateInfo);
    }

    public List<CandidateInfo> findAllCandidateByRoomId(Long roomId) {
        return candidateRepository.findAllByRoomFk(roomId);
    }

    public CandidateInfo findById(String username) {
        return candidateRepository.findById(username).orElse(null);
    }

    public void deleteAllByRoomFk(Long roomId) {
        candidateRepository.deleteAllByRoomFk(roomId);
    }

    public void updateStatusOnlCandidate(String candidateId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<CandidateInfo> criteria = builder.createCriteriaUpdate(CandidateInfo.class);
        Root<CandidateInfo> root = criteria.from(CandidateInfo.class);
        criteria.set("candidateStatus", CandidateStatus.ONLINE);
        criteria.where(builder.equal(root.get("id"), candidateId));
        entityManager.joinTransaction();
        entityManager.createQuery(criteria).executeUpdate();
//        candidateRepository.updateCandidateStatusOnlineById(candidateId);
    }

    public void updateLastSawCandidate(String candidateId, Date lastSaw) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<CandidateInfo> criteria = builder.createCriteriaUpdate(CandidateInfo.class);
        Root<CandidateInfo> root = criteria.from(CandidateInfo.class);
        criteria.set("lastSaw", lastSaw);
        criteria.where(builder.equal(root.get("id"), candidateId));
        entityManager.joinTransaction();
        entityManager.createQuery(criteria).executeUpdate();
//        candidateRepository.updateLastSawById(candidateId, lastSaw);
    }

    public void updateCandidateImageIdNewest(String screenshotId, String faceImgId, String candidateId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<CandidateInfo> criteria = builder.createCriteriaUpdate(CandidateInfo.class);
        Root<CandidateInfo> root = criteria.from(CandidateInfo.class);
        criteria.set("newestScreenShotId", screenshotId);
        criteria.set("newestFaceImgId", faceImgId);
        criteria.where(builder.equal(root.get("id"), candidateId));
        entityManager.joinTransaction();
        entityManager.createQuery(criteria).executeUpdate();
    }

    public void blockCandidate(String candidateId, boolean state) {
        candidateRepository.blockCandidate(state, candidateId);
    }

    public CandidateInfo processUpdateInfo(String candidateId) {
        CandidateInfo candidateInfo = findById(candidateId);
        candidateInfo.setLastSaw(DatnUtils.cvtToGmt(new Date(), 7));
        if (candidateInfo.getCandidateStatus().equals(CandidateStatus.DISCONNECTED) || candidateInfo.getCandidateStatus().equals(CandidateStatus.OFFLINE)) {
            candidateInfo.setCandidateStatus(CandidateStatus.ONLINE);
            updateStatusOnlCandidate(candidateId);
        }
        updateLastSawCandidate(candidateId, candidateInfo.getLastSaw());
//        candidateRepository.updateCandidateStatusOnlineById(candidateInfo.getId());
//        candidateRepository.updateLastSawById(candidateInfo.getId(),candidateInfo.getLastSaw());
        return candidateInfo;
    }
}
