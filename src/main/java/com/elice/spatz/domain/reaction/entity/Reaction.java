package com.elice.spatz.domain.reaction.entity;

import com.elice.spatz.domain.chat.entity.ChatMessage;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reactions")
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "message_id", nullable = false)
    private ChatMessage message;

    @Column(name = "emoji", nullable = false, length = 50)
    private String emoji;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}