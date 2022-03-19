package vn.nuce.datn_be.enity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Entity
@Table(name = "USER")
@Getter
@Setter
public class User extends BaseEntity {
    @Column(name = "USERNAME")
    @NotBlank(message = "Username must not blank")
    String username;

    @Column(name = "EMAIL", unique = true)
    @Email(message = "Email invalid")
    @NotBlank
    String email;

    @Column(name = "PASSWORD")
    @NotEmpty(message = "Password must not empty")
    String password;

    @OneToMany(mappedBy = "ownerId")
    List<ProfileRoom> profileRoomList;

    @OneToMany(mappedBy = "owner")
    List<Room> roomList;

    @OneToMany(mappedBy = "watcherId")
    List<RoomShares> roomShares;
}
