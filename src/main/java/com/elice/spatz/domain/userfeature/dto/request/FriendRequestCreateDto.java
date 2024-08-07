package com.elice.spatz.domain.userfeature.dto.request;

import com.elice.spatz.domain.userfeature.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestCreateDto {
    private long requesterId;
    private long recipientId;
    private Status requestStatus;
}
