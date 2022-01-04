package vn.nuce.datn_be.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.nuce.datn_be.enity.User;
import vn.nuce.datn_be.repositories.UserRepository;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    User findByUsername(String username){
        return userRepository.findByUsername(username).orElse(null);
    }
}
