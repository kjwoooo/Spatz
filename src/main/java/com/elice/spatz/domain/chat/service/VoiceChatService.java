package com.elice.spatz.domain.chat.service;

import com.elice.spatz.domain.chat.repository.VoiceChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.elice.spatz.domain.chat.entity.VoiceChat;
import java.util.List;

@Service
public class VoiceChatService {

    @Autowired
    private VoiceChatRepository voiceChatRepository;

    public VoiceChat createVoiceChat(VoiceChat voiceChat) {
        System.out.println(voiceChat);
//        VoiceChat voiceChat = new VoiceChat();
//        voiceChat.setName(voiceChatDto.getRoomName());
        return voiceChatRepository.save(voiceChat);
    }

    public List<VoiceChat> getAllVoiceChats() {
        return voiceChatRepository.findAll();
    }

    public VoiceChat getVoiceChatById(Long id) {
        return voiceChatRepository.findById(id).orElse(null);
    }
}