package com.elice.spatz.domain.chat.controller;

import com.elice.spatz.domain.chat.entity.VoiceChat;
import com.elice.spatz.domain.chat.service.VoiceChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/chats/api/voiceChats")
@CrossOrigin(origins = "http://localhost:3000")
public class VoiceChatController {

    @Autowired
    private VoiceChatService voiceChatService;

    @PostMapping
    public ResponseEntity<VoiceChat> createVoiceChat(@RequestBody VoiceChat voiceChat) {
        System.out.println("Received request body: " + voiceChat);
        System.out.println("roomName value: " + voiceChat.getName());
        VoiceChat createdVoiceChat = voiceChatService.createVoiceChat(voiceChat);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVoiceChat);
    }

    @GetMapping
    public ResponseEntity<List<VoiceChat>> getAllVoiceChats() {
        List<VoiceChat> voiceChats = voiceChatService.getAllVoiceChats();
        return ResponseEntity.status(HttpStatus.OK).body(voiceChats);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VoiceChat> getVoiceChatById(@PathVariable Long id) {
        VoiceChat voiceChat = voiceChatService.getVoiceChatById(id);
        if (voiceChat != null) {
            return ResponseEntity.ok(voiceChat);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}