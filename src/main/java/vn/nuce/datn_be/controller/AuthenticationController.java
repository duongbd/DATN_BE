package vn.nuce.datn_be.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.nuce.datn_be.enity.CandidateInfo;
import vn.nuce.datn_be.enity.User;
import vn.nuce.datn_be.model.dto.CandidateLoginForm;
import vn.nuce.datn_be.model.dto.MonitorLoginForm;
import vn.nuce.datn_be.services.CandidateService;
import vn.nuce.datn_be.services.UserService;
import vn.nuce.datn_be.utils.JwtUtils;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserService userService;
    @Autowired
    CandidateService candidateService;
    @Autowired
    JwtUtils jwtUtils;

    private String authenticate(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtils.generateJwtToken(authentication);
    }

    @PostMapping(value = "/monitor/login")
    public ResponseEntity<?> loginMonitor(@RequestBody MonitorLoginForm monitorLoginForm) {
        String token = authenticate(monitorLoginForm.getEmail(), monitorLoginForm.getPassword());
        return new ResponseEntity<String>(token, HttpStatus.OK);
    }

    @PostMapping(value = "/candidate/login")
    public ResponseEntity<?> loginCandidate(@RequestBody CandidateLoginForm candidateLoginForm) {
        String token = authenticate(candidateLoginForm.getUsername(), candidateLoginForm.getPassword());
        return new ResponseEntity<String>(token, HttpStatus.OK);
    }

    @PostMapping(value = "/monitor/sign-up")
    public ResponseEntity<?> signUpMonitor(@RequestBody MonitorLoginForm monitorLoginForm) {
        User user = new User();
        user.setUsername(monitorLoginForm.getEmail());
        user.setPassword(monitorLoginForm.getPassword());
        userService.save(user);
        return new ResponseEntity<String>(user.toString(), HttpStatus.OK);
    }

    @PostMapping(value = "/candidate/signUp")
    public ResponseEntity<?> signUp(@RequestBody MonitorLoginForm monitorLoginForm) {
        CandidateInfo candidateInfo = new CandidateInfo();
        candidateInfo.setCandidateName(monitorLoginForm.getEmail());
        candidateInfo.setPassword(monitorLoginForm.getPassword());
        candidateService.save(candidateInfo);
        return new ResponseEntity<String>(candidateInfo.toString(), HttpStatus.OK);
    }

    @PostMapping(value = "/logout")
    public ResponseEntity<?> logoutMonitor() {
        SecurityContextHolder.clearContext();
        return new ResponseEntity<String>(HttpStatus.OK);
    }
}
