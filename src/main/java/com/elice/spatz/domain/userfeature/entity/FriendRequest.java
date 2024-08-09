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
@Table(name = "friendRequest")
public class FriendRequest extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "requesterId", nullable = false)
    private Users requester;

    @ManyToOne
    @JoinColumn(name = "recipientId", nullable = false)
    private Users recipient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status requestStatus;
}
