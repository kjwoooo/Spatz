package com.elice.spatz.domain.chat.mapper;

import com.elice.spatz.domain.chat.dto.ChatChannelDto;
import com.elice.spatz.domain.chat.dto.VoiceChannelDto;
import com.elice.spatz.domain.chat.entity.VoiceChannel;
import com.elice.spatz.domain.server.entity.Servers;

public class VoiceChannelMapper {
    public static VoiceChannelDto toDTO(VoiceChannel voiceChannel) {
        return new VoiceChannelDto(
            voiceChannel.getId(),
            voiceChannel.getName(),
            voiceChannel.getServer() != null ? voiceChannel.getServer().getId() : null
        );
    }

    public static VoiceChannel toEntity(VoiceChannelDto voiceChannelDto, Servers server) {
        VoiceChannel voiceChannel = new VoiceChannel();
        voiceChannel.setName(voiceChannelDto.getName());
        voiceChannel.setServer(server);
        return voiceChannel;
    }

}
