package com.elice.spatz.domain.userfeature.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class BlockDto {
    private long blockerId;
    private long blockedId;
}
