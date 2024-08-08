package com.elice.spatz.domain.userfeature.dto.response;

import com.elice.spatz.domain.userfeature.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FriendRequestDto {
    private Long friendRequestId;
    private Long requesterId;
    private Long recipientId;
    private String requesterNickname;
    private String recipientNickname;
    private Status requestStatus;
}
