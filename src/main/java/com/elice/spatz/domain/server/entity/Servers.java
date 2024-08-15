package com.elice.spatz.domain.server.entity;

import com.elice.spatz.domain.serverUser.entity.ServerUser;
import com.elice.spatz.entity.baseEntity.BaseEntity;
import jakarta.persistence.*;
import java.util.List;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name="servers")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Servers extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    private String inviteCode;

    @OneToMany(mappedBy = "server")
    private List<ServerUser> serverUsers;

}
