package com.elice.spatz.domain.reaction.controller;

import com.elice.spatz.domain.chat.entity.ChatMessage;
import com.elice.spatz.domain.reaction.entity.Reaction;
import com.elice.spatz.domain.reaction.service.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/reactions")
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;

    @GetMapping("/emojiTest")
    public String frontTest(){
        return "/EmojiTest.html";
    }

    @PostMapping
    public ResponseEntity<Reaction> addReaction(@RequestParam String messageId, @RequestParam String emoji) {
        Reaction reaction = reactionService.addReaction(messageId, emoji);
        return ResponseEntity.ok(reaction);
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<List<Reaction>> getReactionsByMessageId(@PathVariable String messageId) {
        List<Reaction> reactions = reactionService.getReactionsByMessageId(messageId);
        return ResponseEntity.ok(reactions);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReaction(@PathVariable Integer id) {
        reactionService.deleteReaction(id);
        return ResponseEntity.ok().build();
    }
}
