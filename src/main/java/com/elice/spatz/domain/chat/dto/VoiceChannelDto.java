package com.elice.spatz.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VoiceChannelDto {
    private Long id;
    private String name;
    private Long serverId;

}
