package com.elice.spatz.domain.reaction.repository;

import com.elice.spatz.domain.chat.entity.ChatMessage;
import com.elice.spatz.domain.reaction.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReactionRepository extends JpaRepository<Reaction, Integer> {

    List<Reaction> findByMessageId(String messageId);
    void deleteByMessageId(String messageId);
    void deleteByMessageIdAndEmoji(String messageId, String emoji);
}