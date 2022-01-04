package vn.nuce.datn_be.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.nuce.datn_be.enity.CandidateInfo;
import vn.nuce.datn_be.repositories.CandidateRepository;

@Service
public class CandidateService {
    @Autowired
    CandidateRepository candidateRepository;

    CandidateInfo findByNumberId(Long numberId){
        return candidateRepository.findByNumberId(numberId).orElse(null);
    }
}
