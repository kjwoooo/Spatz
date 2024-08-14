package com.elice.spatz.domain.chat.controller;

import com.elice.spatz.domain.chat.dto.ChatChannelDto;
import com.elice.spatz.domain.chat.dto.VoiceChannelDto;
import com.elice.spatz.domain.chat.entity.VoiceChannel;
import com.elice.spatz.domain.chat.service.VoiceChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/voiceChannels")
@RequiredArgsConstructor
public class VoiceChannelController {

    private final VoiceChannelService voiceChatService;

    @PostMapping
    public ResponseEntity<VoiceChannelDto> createVoiceChat(@RequestBody VoiceChannelDto voiceChannelDto) {
        System.out.println("Received request body: " + voiceChannelDto);
        System.out.println("roomName value: " + voiceChannelDto.getName());
        VoiceChannelDto createdVoiceChannelDto = voiceChatService.createVoiceChannel(voiceChannelDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVoiceChannelDto);
    }

    @GetMapping("server/{serverId}")
    public ResponseEntity<List<VoiceChannelDto>> getAllVoiceChats(@PathVariable Long serverId) {
        List<VoiceChannelDto> voiceChannelDtoss = voiceChatService.getAllVoiceChannels(serverId);
        return ResponseEntity.status(HttpStatus.OK).body(voiceChannelDtoss);
    }

    @PutMapping("/{voiceChannelId}")
    public ResponseEntity<VoiceChannelDto> updateChannel(@PathVariable Long voiceChannelId, @RequestBody String name) {
        VoiceChannelDto updatedChannelDto = voiceChatService.updateChannel(voiceChannelId, name);
        return ResponseEntity.status(HttpStatus.OK).body(updatedChannelDto);
    }

    @DeleteMapping("/{voiceChannelId}")
    public ResponseEntity<Void> deleteChannel(@PathVariable Long voiceChannelId) {
        voiceChatService.deleteChannel(voiceChannelId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VoiceChannel> getVoiceChatById(@PathVariable Long id) {
        VoiceChannel voiceChat = voiceChatService.getVoiceChatById(id);
        if (voiceChat != null) {
            return ResponseEntity.ok(voiceChat);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}