package com.elice.spatz.domain.chat.controller;


import com.elice.spatz.config.CustomUserDetails;
import com.elice.spatz.domain.chat.entity.ChatMessage;
import com.elice.spatz.domain.chat.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;


    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
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
    public void joinChannel(@Payload ChatMessage chatMessage,
                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String channelId = chatMessage.getChannelId();


    }


    // 채널의 최근 메시지 50개를 조회하는 (REST API)
    // 채널의 모든 메시지를 조회하는 (REST API)
    // 특정 사용자가 특정 채널에서 보낸 메시지를 조회하는 (REST API)
    // 메시지를 수정하는 (REST API)
    // 메시지를 삭제하는 (REST API)





}



