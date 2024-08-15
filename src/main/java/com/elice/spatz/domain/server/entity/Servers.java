package com.elice.spatz.domain.server.entity;

import com.elice.spatz.domain.serverUser.entity.ServerUser;
import com.elice.spatz.entity.baseEntity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name="servers")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Servers extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    private String inviteCode;

    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL)
    private List<ServerUser> serverUsers;

}
