package vn.nuce.datn_be.enity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "APP")
public class App extends BaseEntity {
    @Column(unique = true)
    String appName;
    @Column
    String processName;

    @OneToMany(mappedBy = "appFk")
    private List<RoomAppKey> roomAppKeys;
}
