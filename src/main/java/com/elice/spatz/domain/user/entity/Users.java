package com.elice.spatz.domain.user.entity;

import com.elice.spatz.domain.userfeature.model.entity.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="users")
@NoArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String email;
    private String password;
    private String nickname;
    private LocalDateTime lastLogin;
    private boolean isOnConnection;
    private String role;
    private boolean isActivated;

    public Users(String email, String password, String nickname, LocalDateTime lastLogin, boolean isOnConnection, String role, boolean isActivated) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.lastLogin = lastLogin;
        this.isOnConnection = isOnConnection;
        this.role = role;
        this.isActivated = isActivated;
    }

    // 친구 요청
    @OneToMany(mappedBy = "requester", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FriendRequest> receivedFriendRequests;

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FriendRequest> sentFriendRequests;

    // 친구 관계
    @OneToMany(mappedBy = "friend", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Friendship> friendships;

    // 신고
    @OneToMany(mappedBy = "reporter", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Report> reportedList;
    @OneToMany(mappedBy = "reported", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Report> recievedList;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ReportCount reportCount;

    // 차단
    @OneToMany(mappedBy = "blocker", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Block> blockUsers;

    // 정지
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private BannedUser bannedUser;
}
