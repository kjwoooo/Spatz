package com.elice.spatz.domain.userfeature.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BlockCreateDto {
    private Long blockerId;
    private Long blockedId;
}
