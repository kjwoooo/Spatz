package com.elice.spatz.domain.chat.service;

import com.elice.spatz.domain.chat.dto.VoiceChannelDto;
import com.elice.spatz.domain.chat.mapper.VoiceChannelMapper;
import com.elice.spatz.domain.chat.repository.VoiceChannelRepository;
import com.elice.spatz.domain.server.entity.Servers;
import com.elice.spatz.domain.server.repository.ServerRepository;
import com.elice.spatz.exception.errorCode.VoiceChannelErrorCode;
import com.elice.spatz.exception.exception.VoiceChannelException;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.elice.spatz.domain.chat.entity.VoiceChannel;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoiceChannelService {

    private final VoiceChannelRepository voiceChannelRepository;
    private final ServerRepository serverRepository;

    public VoiceChannelDto createVoiceChannel(VoiceChannelDto voiceChannelDto) {
        if(voiceChannelDto.getName() == null || voiceChannelDto.getName().trim().isEmpty()) {
            throw new VoiceChannelException(VoiceChannelErrorCode.INVALID_CHANNEL_NAME);
        }

        Servers server = serverRepository.findById(voiceChannelDto.getServerId())
            .orElseThrow(() -> new VoiceChannelException(VoiceChannelErrorCode.SERVER_NOT_FOUND));

        // DTO -> 엔티티로
        VoiceChannel channel = VoiceChannelMapper.toEntity(voiceChannelDto, server);
        VoiceChannel savedVoiceChannel = voiceChannelRepository.save(channel);
        return VoiceChannelMapper.toDTO(savedVoiceChannel);
    }

    public List<VoiceChannelDto> getAllVoiceChannels(Long serverId) {
        List<VoiceChannel> channels = voiceChannelRepository.findAllByServerId(serverId);
        List<VoiceChannelDto> channelDtos = new ArrayList<>();

        for (VoiceChannel channel : channels) {
            channelDtos.add(VoiceChannelMapper.toDTO(channel));
        }

        return channelDtos;
    }

    public VoiceChannel getVoiceChatById(Long id) {
        return voiceChannelRepository.findById(id).orElse(null);
    }

    public VoiceChannelDto updateChannel(Long id, String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new VoiceChannelException(VoiceChannelErrorCode.INVALID_CHANNEL_NAME);
        }

        VoiceChannel channel = voiceChannelRepository.findById(id)
            .orElseThrow(() -> new VoiceChannelException(VoiceChannelErrorCode.CHANNEL_NOT_FOUND));

        channel.setName(name);
        VoiceChannel updatedChannel = voiceChannelRepository.save(channel);
        return VoiceChannelMapper.toDTO(updatedChannel);
    }

    // 채널을 삭제
    public void deleteChannel(Long id) {
        if (!voiceChannelRepository.existsById(id)) {
            throw new VoiceChannelException(VoiceChannelErrorCode.CHANNEL_NOT_FOUND);
        }
        voiceChannelRepository.deleteById(id);
    }
}