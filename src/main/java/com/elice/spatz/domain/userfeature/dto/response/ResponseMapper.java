package com.elice.spatz.domain.userfeature.dto.response;

import com.elice.spatz.domain.userfeature.entity.Block;
import com.elice.spatz.domain.userfeature.entity.FriendRequest;
import com.elice.spatz.domain.userfeature.entity.Friendship;
import com.elice.spatz.domain.userfeature.entity.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ResponseMapper {
    ResponseMapper INSTANCE = Mappers.getMapper(ResponseMapper.class);

    // 차단 blockId
    @Mapping(source = "id", target = "blockId")
    @Mapping(source = "blocker.id", target = "blockerId")
    @Mapping(source = "blocked.id", target = "blockedId")
    @Mapping(source = "blocked.nickname", target = "blockedNickname")
    BlockDto blockToBlockDto(Block entity);

    // 친구 요청
    @Mapping(source = "id", target = "friendRequestId")
    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "recipient.id", target = "recipientId")
    @Mapping(source = "recipient.nickname", target = "requesterNickname")
    @Mapping(source = "recipient.nickname", target = "recipientNickname")
    @Mapping(source = "requestStatus", target = "requestStatus")
    FriendRequestDto friendRequestToFriendRequestDto(FriendRequest entity);

    // 친구
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "friend.id", target = "friendId")
    @Mapping(source = "user.nickname", target = "userNickname")
    @Mapping(source = "friend.nickname", target = "friendNickname")
    FriendDto friendshipToFriendDto(Friendship entity);

    // 신고
    @Mapping(source = "reporter.id", target = "reporterId")
    @Mapping(source = "reported.id", target = "reportedId")
    @Mapping(source = "reported.nickname", target = "reportedNickname")
    @Mapping(source = "reportStatus", target = "reportStatus")
    @Mapping(source = "reportReason", target = "reportReason")
    @Mapping(source = "reportImage", target = "reportImage")
    ReportDto reportToReportDto(Report entity);
}
