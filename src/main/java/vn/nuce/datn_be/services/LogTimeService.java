package vn.nuce.datn_be.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.nuce.datn_be.enity.LogTime;
import vn.nuce.datn_be.repositories.LogTimeRepository;

@Service
public class LogTimeService {
    @Autowired
    private LogTimeRepository logTimeRepository;

    public LogTime save(LogTime logTime) {
        return logTimeRepository.save(logTime);
    }
}
