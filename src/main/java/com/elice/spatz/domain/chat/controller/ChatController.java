package com.elice.spatz.domain.chat.controller;

import com.elice.spatz.domain.chat.entity.ChatMessage;
import com.elice.spatz.domain.chat.service.ChatService;
import com.elice.spatz.domain.reaction.service.ReactionService;
import com.elice.spatz.domain.reaction.util.EmojiUtils;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

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
    /**
     * 채팅방에 들어올때
     * 추후 로그인한 사용자 정보 가져와야함
     */
    @MessageMapping("/chat/enter")
    public void enterChatChannel(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {

        // 웹소켓 세션에 username, senderId, channelId 저장
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSenderId());
        // userService에서 username을 받아오는걸로 수정
        headerAccessor.getSessionAttributes().put("senderId", chatMessage.getSenderId());
        headerAccessor.getSessionAttributes().put("channelId", chatMessage.getChannelId());

        //username을 받아와서 입장시 메세지 수정
        chatMessage.setContent(chatMessage.getSenderId() + "님이 입장하셨습니다.");
        messagingTemplate.convertAndSend("/topic/chat/" + chatMessage.getChannelId(), chatMessage);

    }

    /**
     * 클라이언트에서 받아온 메세지를 db에 저장후 브로드캐스트
     */
    @MessageMapping("/chat/send")
    public void sendMessage(@Payload ChatMessage chatMessage) {

        // 이모지 추출 및 문자 분리
        List<String> emojis = EmojiUtils.extractEmojis(chatMessage.getContent());
        String textContent = EmojiUtils.removeEmojis(chatMessage.getContent());

        // 이모지를 ReactionService를 통해 MySQL에 저장
        for (String emoji : emojis) {
            reactionService.addReaction(chatMessage.getId().toString(), emoji);
        }

        // 문자가 포함된 메시지를 Redis에 저장
        chatMessage.setContent(textContent);
        ChatMessage savedMessage = chatService.sendAndSaveMessage(chatMessage);

        messagingTemplate.convertAndSend("/topic/chat/" + chatMessage.getChannelId(), savedMessage);
    }


    /**
     * 클라이언트에서 받아온 메세지 수정후 db에 저장 -> 브로드캐스트
     */
    @MessageMapping("/chat/edit")
    public void updateMessage(@Payload ChatMessage chatMessage) {

        // 기존 메시지를 DB에서 가져오기
        ChatMessage existingMessage = chatService.findMessageById(chatMessage.getChannelId(), chatMessage.getId());

        // 기존 메시지에서 이모지를 추출
        List<String> existingEmojis = EmojiUtils.extractEmojis(existingMessage.getContent());

        // 새로운 메시지에서 이모지를 추출
        List<String> newEmojis = EmojiUtils.extractEmojis(chatMessage.getContent());

        // 기존 이모지와 새로운 이모지를 비교하여 추가,삭제
        // 추가된 이모지
        for (String emoji : newEmojis) {
            if (!existingEmojis.contains(emoji)) {
                reactionService.addReaction(chatMessage.getId().toString(), emoji);
            }
        }

        // 삭제된 이모지
        for (String emoji : existingEmojis) {
            if (!newEmojis.contains(emoji)) {
                reactionService.deleteReactionByMessageIdAndEmoji(chatMessage.getId().toString(), emoji);
            }
        }

        // 텍스트 부분 업데이트
        String textContent = EmojiUtils.removeEmojis(chatMessage.getContent());
        chatMessage.setContent(textContent);


        ChatMessage updatedMessage = chatService.updatedMessage(
                chatMessage.getChannelId(),
                chatMessage.getId(),
                chatMessage.getContent()
        );

        messagingTemplate.convertAndSend("/topic/chat/" + chatMessage.getChannelId() + "/update", updatedMessage);
    }

    /**
     * 클라이언트에서 삭제요청 db에서 삭제 후 -> 브로드캐스트
     */
    @MessageMapping("/chat/delete")
    public void deleteMessage(@Payload ChatMessage chatMessage) {
        Long result = chatService.deleteMessage(chatMessage.getChannelId(), chatMessage.getId());

        // 연결된 이모지도 삭제
        reactionService.deleteReactionsByMessageId(chatMessage.getId().toString());


        if(result > 0) {
            messagingTemplate.convertAndSend("/topic/chat/" + chatMessage.getChannelId() + "/delete", chatMessage);
        }


    }

}


