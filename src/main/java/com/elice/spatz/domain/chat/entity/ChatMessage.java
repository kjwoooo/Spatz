package com.elice.spatz.domain.chat.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class ChatMessage implements Serializable {
        private String id;
        private String channelId;
        private String senderId;
        private String senderName;
        private String content;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdTime;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updatedTime;
        private boolean isDeleted;
        private boolean isEdited;

        // 메시지 수정 메서드
        public void updateContent(String newContent) {
                this.content = newContent;
                this.updatedTime = LocalDateTime.now();
                this.isEdited = true;
        }

        // 메시지 삭제 메서드
        public void deleteMessage() {
                this.isDeleted = true;
                this.updatedTime = LocalDateTime.now();
        }

    }
