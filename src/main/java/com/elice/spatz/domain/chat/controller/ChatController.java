package com.elice.spatz.domain.chat.controller;


import com.elice.spatz.config.CustomUserDetails;
import com.elice.spatz.domain.chat.entity.ChatMessage;
import com.elice.spatz.domain.chat.service.ChatService;
import com.elice.spatz.domain.reaction.service.ReactionService;
import com.elice.spatz.domain.reaction.util.EmojiUtils;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ReactionService reactionService; // ReactionService 추가


    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate, ReactionService reactionService) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
        this.reactionService = reactionService;
    }

    // 웹소켓을 통해 메시지를 받아 처리 (@MessageMapping)
    @MessageMapping("chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage,
                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 유저 id, name 설정
        chatMessage.setSenderId(userDetails.getId().toString());
        chatMessage.setSenderName(userDetails.getUsername());

        ChatMessage savedMessage = chatService.SaveMessage(chatMessage);
        // 저장된 메세지를 해당 채널의 구독자에게 보냄
        messagingTemplate.convertAndSend("/topic/channel/" + chatMessage.getChannelId(), savedMessage);
    }

    // 사용자가 채널에 입장할 때 호출되는 메서드 (@MessageMapping)
    @MessageMapping("chat.joinChannel")
    public void joinChannel(@Payload ChatMessage chatMessage,
                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String channelId = chatMessage.getChannelId();
        String joinMessage = userDetails.getUsername() + " 님이 채널에 입장했습니다.";

        // 입장 메시지를 해당 채널의 구독자에게 보냄
        messagingTemplate.convertAndSend("/topic/channel/" + channelId, joinMessage);
    }

    // 채널의 최근 메시지 50개를 조회하는 REST API
    @GetMapping("/{channelId}/recent")
    @ResponseBody
    public List<ChatMessage> getRecentMessages(@PathVariable String channelId) {
        return chatService.getRecentMessages(channelId);
    }

    // 채널의 모든 메시지를 조회하는 REST API
    @GetMapping("/{channelId}/all")
    @ResponseBody
    public List<ChatMessage> getAllMessages(@PathVariable String channelId) {
        return chatService.getAllMessagesInChannel(channelId);
    }

    // 특정 사용자가 특정 채널에서 보낸 메시지를 조회 (REST API)
    @GetMapping("/{channelId}/user/{senderId}")
    @ResponseBody
    public List<ChatMessage> getUserMessages(@PathVariable String channelId, @PathVariable String senderId) {
        return chatService.getMessagesBySender(channelId, senderId);
    }

    // 메시지 수정 (REST API)
    @PutMapping("/{channelId}/{messageId}")
    @ResponseBody
    public ChatMessage updateMessage(@PathVariable String channelId,
                                     @PathVariable String messageId,
                                     @RequestBody String newContent) {
        return chatService.updateMessage(channelId, messageId, newContent);
    }

    // 메시지 삭제 (REST API)
    @DeleteMapping("/{channelId}/{messageId}")
    @ResponseBody
    public ChatMessage deleteMessage(@PathVariable String channelId, @PathVariable String messageId) {
        return chatService.deleteMessage(channelId, messageId);
    }
}
