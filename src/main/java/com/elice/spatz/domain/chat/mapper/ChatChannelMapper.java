package com.elice.spatz.domain.chat.mapper;

import com.elice.spatz.domain.chat.dto.ChatChannelDto;
import com.elice.spatz.domain.chat.entity.ChatChannel;
import com.elice.spatz.domain.server.entity.Servers;

public class ChatChannelMapper {
    public static ChatChannelDto toDTO(ChatChannel chatChannel) {
        return new ChatChannelDto(
                chatChannel.getId(),
                chatChannel.getName(),
                chatChannel.getServer() != null ? chatChannel.getServer().getId() : null
        );
    }

    public static ChatChannel toEntity(ChatChannelDto chatChannelDto, Servers server) {
        ChatChannel chatChannel = new ChatChannel();
        chatChannel.setName(chatChannelDto.getName());
        chatChannel.setServer(server);
        return chatChannel;
    }
}