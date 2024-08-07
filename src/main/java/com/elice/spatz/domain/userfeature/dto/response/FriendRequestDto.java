package com.elice.spatz.domain.userfeature.dto.response;

import com.elice.spatz.domain.userfeature.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FriendRequestDto {
    private Long requesterId;
    private Long recipientId;
    private Status requestStatus;
}
