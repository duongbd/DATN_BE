package vn.nuce.datn_be.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.nuce.datn_be.enity.User;
import vn.nuce.datn_be.repositories.UserRepository;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public User findByUsername(String username){
        return userRepository.findByUsername(username).orElse(null);
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email).orElse(null);
    }

    public User save(User user){
        return userRepository.save(user);
    }
}
