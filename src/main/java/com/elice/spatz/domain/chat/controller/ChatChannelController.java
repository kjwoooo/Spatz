package com.elice.spatz.domain.chat.controller;

import com.elice.spatz.domain.chat.dto.ChatChannelDto;
import com.elice.spatz.domain.chat.service.ChatChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/channels")
public class ChatChannelController {

    private final ChatChannelService chatChannelService;

    @Autowired
    public ChatChannelController(ChatChannelService chatChannelService) {
        this.chatChannelService = chatChannelService;
    }

    /**
     * 채널 생성
     */
    @PostMapping
    public ResponseEntity<ChatChannelDto> createChannel(@RequestBody ChatChannelDto channelDto) {
        ChatChannelDto createdChannel = chatChannelService.createChannel(channelDto);
        return new ResponseEntity<>(createdChannel, HttpStatus.CREATED);
    }

    /**
     * 채널 ID로 채널 조회
     */
    @GetMapping("/{channelId}")
    public ResponseEntity<ChatChannelDto> getChannel(@PathVariable Long channelId) {
        ChatChannelDto channel = chatChannelService.getChannel(channelId);
        return ResponseEntity.ok(channel);
    }

    /**
     * 서버 ID로 채널 조회
     */
    @GetMapping("/server/{serverId}")
    public ResponseEntity<List<ChatChannelDto>> getChannelsByServerId(@PathVariable Long serverId) {
        List<ChatChannelDto> channels = chatChannelService.getChannelsByServerId(serverId);
        return ResponseEntity.ok(channels);
    }

    /**
     * 채널 이름 수정
     */
    @PutMapping("/{channelId}")
    public ResponseEntity<ChatChannelDto> updateChannel(@PathVariable Long channelId, @RequestBody ChatChannelDto channelDto) {
        ChatChannelDto updatedChannel = chatChannelService.updateChannel(channelId, channelDto);
        return ResponseEntity.ok(updatedChannel);
    }

    /**
     * 채널 삭제
     */
    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> deleteChannel(@PathVariable Long channelId) {
        chatChannelService.deleteChannel(channelId);
        return ResponseEntity.noContent().build();
    }


}
