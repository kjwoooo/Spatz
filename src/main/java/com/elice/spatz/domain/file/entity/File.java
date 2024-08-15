package com.elice.spatz.domain.file.entity;

import com.elice.spatz.domain.chat.entity.ChatChannel;
import com.elice.spatz.domain.user.entity.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "file")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // 추가
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = true)
    @JsonIgnore // 직렬화에서 제외
    private ChatChannel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    @JsonIgnore // 직렬화에서 제외
    private Users user;  // 유저 필드 추가

    private String fileName;
    private String fileKey;
    private String storageUrl;

    @Column(updatable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();
}
