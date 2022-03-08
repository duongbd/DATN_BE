package vn.nuce.datn_be.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.nuce.datn_be.enity.App;
import vn.nuce.datn_be.repositories.AppRepository;

import java.util.List;

@Service
public class AppService {
    @Autowired
    AppRepository appRepository;

    public List<App> findAllByAppNameIn(List<String> appNames){
        return findAllByAppNameIn(appNames);
    }

    public List<App> findAll(){
        return appRepository.findAll();
    }
}
