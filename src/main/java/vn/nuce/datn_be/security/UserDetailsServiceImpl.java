package vn.nuce.datn_be.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.nuce.datn_be.enity.CandidateInfo;
import vn.nuce.datn_be.enity.User;
import vn.nuce.datn_be.repositories.CandidateRepository;
import vn.nuce.datn_be.repositories.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    CandidateRepository candidateRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            CandidateInfo candidate = candidateRepository.findByNumberId(Long.parseLong(username)).orElseThrow(() -> new UsernameNotFoundException("User not found with numberId: " + username));
            return UserDetailsImpl.build(candidate);
        }
        return UserDetailsImpl.build(user);
    }
}
