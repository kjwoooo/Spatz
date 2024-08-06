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

    public Reaction addReaction(ChatMessage message, String emoji) {
        Reaction reaction = new Reaction();
        reaction.setMessage(message);
        reaction.setEmoji(emoji);
        return reactionRepository.save(reaction);
    }

    public List<Reaction> getReactionsByMessage(ChatMessage message) {
        return reactionRepository.findByMessage(message);
    }

    public void deleteReaction(Integer id) {
        reactionRepository.deleteById(id);
    }
}
