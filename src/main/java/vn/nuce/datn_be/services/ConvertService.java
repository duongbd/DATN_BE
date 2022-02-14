package vn.nuce.datn_be.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.nuce.datn_be.enity.Room;
import vn.nuce.datn_be.model.dto.RoomForm;
import vn.nuce.datn_be.repositories.CandidateRepository;
import vn.nuce.datn_be.repositories.RoomRepository;
import vn.nuce.datn_be.repositories.UserRepository;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Service
public class ConvertService {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

}
