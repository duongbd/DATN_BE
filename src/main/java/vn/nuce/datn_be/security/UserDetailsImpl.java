package vn.nuce.datn_be.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vn.nuce.datn_be.enity.CandidateInfo;
import vn.nuce.datn_be.enity.User;
import vn.nuce.datn_be.model.enumeration.RoleEnum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID= 1L;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    private String username;

    private long numberId;

    private Collection<? extends GrantedAuthority> authorities;

    public static UserDetailsImpl build(User user){
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(RoleEnum.MONITOR.name()));

        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setUsername(user.getUsername());
        userDetails.setAuthorities(authorities);
        return userDetails;
    }

    public static UserDetailsImpl build(CandidateInfo candidateInfo){
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(RoleEnum.CANDIDATE.name()));

        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setNumberId(candidateInfo.getNumberId());
        userDetails.setAuthorities(authorities);
        return userDetails;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
