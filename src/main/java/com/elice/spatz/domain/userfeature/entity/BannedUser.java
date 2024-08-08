package com.elice.spatz.domain.userfeature.entity;

import com.elice.spatz.domain.user.entity.Users;
import com.elice.spatz.entity.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bannedUser")
public class BannedUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본 키

    @OneToOne
    @JoinColumn(name = "userId", nullable = false)
    private Users user; // Users 엔티티의 ID를 참조하는 필드

    @Column(nullable = false)
    private LocalDateTime bannedStart;

    @Column(nullable = false)
    private LocalDateTime bannedEnd;

    public BannedUser(Users user, LocalDateTime bannedStart, LocalDateTime bannedEnd) {
        this.user = user;
        this.bannedStart = bannedStart;
        this.bannedEnd = bannedEnd;
    }
}
