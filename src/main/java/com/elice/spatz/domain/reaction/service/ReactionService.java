package com.elice.spatz.domain.reaction.service;

import com.elice.spatz.domain.chat.entity.ChatMessage;
import com.elice.spatz.domain.reaction.entity.Reaction;
import com.elice.spatz.domain.reaction.repository.ReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.print.DocFlavor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReactionService {
    private final ReactionRepository reactionRepository;

    public Reaction addReaction(String messageId, String emoji) {
        Reaction reaction = new Reaction();
        reaction.setMessageId(messageId);
        reaction.setEmoji(emoji);
        return reactionRepository.save(reaction);
    }

    public List<Reaction> getReactionsByMessageId(String messageId) {
        return reactionRepository.findByMessageId(messageId);
    }


    public void deleteReaction(Integer id) {
        reactionRepository.deleteById(id);
    }
}
