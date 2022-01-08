package vn.nuce.datn_be.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;

@Getter
@Setter
public class MonitorLoginForm {
    @Email
    String email;
    String password;
}
