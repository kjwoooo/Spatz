package com.elice.spatz.domain.file.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class FileRequestDto {
    private String messageId;
    private Long channelId;
    private Long userId;
}
