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
    public ResponseEntity<Reaction> addReaction(@RequestParam ChatMessage message, @RequestParam String emoji) {
        Reaction reaction = reactionService.addReaction(message, emoji);
        return ResponseEntity.ok(reaction);
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<List<Reaction>> getReactionsByMessageId(@PathVariable ChatMessage message) {
        List<Reaction> reactions = reactionService.getReactionsByMessage(message);
        return ResponseEntity.ok(reactions);
    }
}
