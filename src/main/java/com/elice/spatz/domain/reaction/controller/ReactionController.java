package com.elice.spatz.domain.reaction.controller;

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
        return "/index.html";
    }

    @PostMapping
    public ResponseEntity<Reaction> addReaction(@RequestParam("messageId") String messageId, @RequestParam("emoji") String emoji) {
        Reaction reaction = reactionService.addReaction(messageId, emoji);
        return ResponseEntity.ok(reaction);
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<List<Reaction>> getReactionsByMessageId(@PathVariable("messageId") String messageId) {
        List<Reaction> reactions = reactionService.getReactionsByMessageId(messageId);
        return ResponseEntity.ok(reactions);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReaction(@PathVariable("id") Integer id) {
        reactionService.deleteReaction(id);
        return ResponseEntity.ok().build();
    }
}
