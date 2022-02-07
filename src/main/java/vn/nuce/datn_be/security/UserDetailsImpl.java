package vn.nuce.datn_be.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vn.nuce.datn_be.enity.CandidateInfo;
import vn.nuce.datn_be.enity.User;
import vn.nuce.datn_be.model.enumeration.CandidateStatus;
import vn.nuce.datn_be.model.enumeration.RoleEnum;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    private String name;

    //only for candidate
    private long candidateNumberId;

    private String password;

    private boolean blocked = false;

    // for monitor
    private Long monitorId;

    //for candidate
    private String candidateId;

    private Collection<? extends GrantedAuthority> authorities;

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(RoleEnum.MONITOR.name()));

        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setName(user.getEmail());
        userDetails.setAuthorities(authorities);
        userDetails.setPassword(user.getPassword());
        userDetails.setMonitorId(user.getId());
        return userDetails;
    }

    public static UserDetailsImpl build(CandidateInfo candidateInfo) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(RoleEnum.CANDIDATE.name()));

        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setName(candidateInfo.getId());
        userDetails.setCandidateNumberId(candidateInfo.getNumberId());
        userDetails.setAuthorities(authorities);
        userDetails.setPassword(candidateInfo.getPassword());
        userDetails.setCandidateId(candidateInfo.getId());
        if (candidateInfo.getCandidateStatus() != null && candidateInfo.getCandidateStatus().equals(CandidateStatus.BLOCK)) {
            userDetails.setBlocked(true);
        }
        return userDetails;
    }

    @Override
    public String getPassword() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isBlocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
