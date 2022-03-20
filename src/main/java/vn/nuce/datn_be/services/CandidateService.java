package vn.nuce.datn_be.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nuce.datn_be.enity.CandidateInfo;
import vn.nuce.datn_be.repositories.CandidateRepository;

import java.util.List;

@Transactional
@Service
public class CandidateService {
    @Autowired
    CandidateRepository candidateRepository;

    public CandidateInfo findByNumberId(Long numberId){
        return candidateRepository.findByNumberId(numberId).orElse(null);
    }

    public CandidateInfo save(CandidateInfo candidateInfo){
        return candidateRepository.save(candidateInfo);
    }

    public List<CandidateInfo> findAllCandidateByRoomId(Long roomId){
        return candidateRepository.findAllByRoomFk(roomId);
    }

    public CandidateInfo findById(String username){
        return candidateRepository.findById(username).orElse(null);
    }

    public void deleteAllByRoomFk(Long roomId){
        candidateRepository.deleteAllByRoomFk(roomId);
    }
}
