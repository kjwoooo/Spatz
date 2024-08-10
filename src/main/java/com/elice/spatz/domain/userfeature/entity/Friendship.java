package com.elice.spatz.domain.userfeature.entity;

import com.elice.spatz.domain.user.entity.Users;
import com.elice.spatz.entity.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "friendship")
public class Friendship extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "friendId", nullable = false)
    private Users friend;

    @Column(nullable = false)
    private boolean friendStatus;
}
