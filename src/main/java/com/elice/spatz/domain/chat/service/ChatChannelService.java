package com.elice.spatz.domain.chat.service;

import com.elice.spatz.domain.chat.dto.ChatChannelDto;
import com.elice.spatz.domain.chat.entity.ChatChannel;
import com.elice.spatz.domain.chat.mapper.ChatChannelMapper;
import com.elice.spatz.domain.chat.repository.ChatChannelRepository;
import com.elice.spatz.domain.server.entity.Servers;
import com.elice.spatz.domain.server.repository.ServerRepository;
import com.elice.spatz.exception.errorCode.ChannelErrorCode;
import com.elice.spatz.exception.exception.ChannelException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatChannelService {

    private final ChatChannelRepository chatChannelRepository;
    private final ServerRepository serverRepository;

    @Autowired
    public ChatChannelService(ChatChannelRepository chatChannelRepository, ServerRepository serverRepository) {
        this.chatChannelRepository = chatChannelRepository;
        this.serverRepository = serverRepository;
    }

    // 새로운 채팅 채널을 생성
    public ChatChannelDto createChannel(ChatChannelDto channelDto) {
        if (channelDto.getName() == null || channelDto.getName().trim().isEmpty()) {
            throw new ChannelException(ChannelErrorCode.INVALID_CHANNEL_NAME);
        }

        // 서버 ID 이용 우선 serverRepository 에서 찾아옴.
        Servers server = serverRepository.findById(channelDto.getServerId())
                .orElseThrow(() -> new ChannelException(ChannelErrorCode.SERVER_NOT_FOUND));

        // DTO -> 엔티티로
        ChatChannel channel = ChatChannelMapper.toEntity(channelDto, server);

        ChatChannel savedChannel = chatChannelRepository.save(channel);
        return ChatChannelMapper.toDTO(savedChannel);
    }

    // 채널 ID로 채널을 조회
    public ChatChannelDto getChannel(Long channelId) {
        ChatChannel channel = chatChannelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelException(ChannelErrorCode.CHANNEL_NOT_FOUND));

        return ChatChannelMapper.toDTO(channel);
    }

    // ChatMessage의 channelId로 사용할 수 있는 문자열 ID를 반환
    public String getChannelId(Long channelId) {
        ChatChannel channel = chatChannelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelException(ChannelErrorCode.CHANNEL_NOT_FOUND));
        return channel.getId().toString();
    }

    // 서버 ID로 해당 서버의 모든 채널을 조회
    public List<ChatChannelDto> getChannelsByServerId(Long serverId) {
        List<ChatChannel> channels = chatChannelRepository.findAllByServerId(serverId);
        List<ChatChannelDto> channelDtos = new ArrayList<>();

        for (ChatChannel channel : channels) {
            channelDtos.add(ChatChannelMapper.toDTO(channel));
        }

        return channelDtos;
    }

    // 채널 업데이트 (채널 이름 변경)
    public ChatChannelDto updateChannel(Long id, ChatChannelDto channelDto) {
        if (channelDto.getName() == null || channelDto.getName().trim().isEmpty()) {
            throw new ChannelException(ChannelErrorCode.INVALID_CHANNEL_NAME);
        }

        ChatChannel channel = chatChannelRepository.findById(id)
                .orElseThrow(() -> new ChannelException(ChannelErrorCode.CHANNEL_NOT_FOUND));

        channel.setName(channelDto.getName());
        ChatChannel updatedChannel = chatChannelRepository.save(channel);
        return ChatChannelMapper.toDTO(updatedChannel);
    }

    // 채널을 삭제
    public void deleteChannel(Long id) {
        if (!chatChannelRepository.existsById(id)) {
            throw new ChannelException(ChannelErrorCode.CHANNEL_NOT_FOUND);
        }
        chatChannelRepository.deleteById(id);
    }
}
