package com.elice.spatz.domain.userfeature.model.dto.response;

import com.elice.spatz.domain.user.entity.Users;
import com.elice.spatz.domain.userfeature.model.entity.Block;
import com.elice.spatz.domain.userfeature.model.entity.FriendRequest;
import com.elice.spatz.domain.userfeature.model.entity.Friendship;
import com.elice.spatz.domain.userfeature.model.entity.Report;
import com.elice.spatz.domain.userfeature.model.entity.Status;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-08-05T20:17:53+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class ResponseMapperImpl implements ResponseMapper {

    @Override
    public BlockDto blockToBlockDto(Block entity) {
        if ( entity == null ) {
            return null;
        }

        long blockerId = 0L;
        long blockedId = 0L;

        Long id = entityBlockerId( entity );
        if ( id != null ) {
            blockerId = id;
        }
        Long id1 = entityBlockedId( entity );
        if ( id1 != null ) {
            blockedId = id1;
        }

        BlockDto blockDto = new BlockDto( blockerId, blockedId );

        return blockDto;
    }

    @Override
    public FriendRequestDto friendRequestToFriendRequestDto(FriendRequest entity) {
        if ( entity == null ) {
            return null;
        }

        Long requesterId = null;
        Long recipientId = null;
        Status requestStatus = null;

        requesterId = entityRequesterId( entity );
        recipientId = entityRecipientId( entity );
        requestStatus = entity.getRequestStatus();

        FriendRequestDto friendRequestDto = new FriendRequestDto( requesterId, recipientId, requestStatus );

        return friendRequestDto;
    }

    @Override
    public FriendDto friendshipToFriendDto(Friendship entity) {
        if ( entity == null ) {
            return null;
        }

        Long userId = null;
        Long friendId = null;
        String userNickname = null;
        String friendNickname = null;

        userId = entityUserId( entity );
        friendId = entityFriendId( entity );
        userNickname = entityUserNickname( entity );
        friendNickname = entityFriendNickname( entity );

        FriendDto friendDto = new FriendDto( userId, friendId, userNickname, friendNickname );

        return friendDto;
    }

    @Override
    public ReportDto reportToReportDto(Report entity) {
        if ( entity == null ) {
            return null;
        }

        Long reporterId = null;
        Long reportedId = null;
        String reportReason = null;

        reporterId = entityReporterId( entity );
        reportedId = entityReportedId( entity );
        reportReason = entity.getReportReason();

        ReportDto reportDto = new ReportDto( reporterId, reportedId, reportReason );

        return reportDto;
    }

    private Long entityBlockerId(Block block) {
        if ( block == null ) {
            return null;
        }
        Users blocker = block.getBlocker();
        if ( blocker == null ) {
            return null;
        }
        Long id = blocker.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long entityBlockedId(Block block) {
        if ( block == null ) {
            return null;
        }
        Users blocked = block.getBlocked();
        if ( blocked == null ) {
            return null;
        }
        Long id = blocked.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long entityRequesterId(FriendRequest friendRequest) {
        if ( friendRequest == null ) {
            return null;
        }
        Users requester = friendRequest.getRequester();
        if ( requester == null ) {
            return null;
        }
        Long id = requester.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long entityRecipientId(FriendRequest friendRequest) {
        if ( friendRequest == null ) {
            return null;
        }
        Users recipient = friendRequest.getRecipient();
        if ( recipient == null ) {
            return null;
        }
        Long id = recipient.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long entityUserId(Friendship friendship) {
        if ( friendship == null ) {
            return null;
        }
        Users user = friendship.getUser();
        if ( user == null ) {
            return null;
        }
        Long id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long entityFriendId(Friendship friendship) {
        if ( friendship == null ) {
            return null;
        }
        Users friend = friendship.getFriend();
        if ( friend == null ) {
            return null;
        }
        Long id = friend.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String entityUserNickname(Friendship friendship) {
        if ( friendship == null ) {
            return null;
        }
        Users user = friendship.getUser();
        if ( user == null ) {
            return null;
        }
        String nickname = user.getNickname();
        if ( nickname == null ) {
            return null;
        }
        return nickname;
    }

    private String entityFriendNickname(Friendship friendship) {
        if ( friendship == null ) {
            return null;
        }
        Users friend = friendship.getFriend();
        if ( friend == null ) {
            return null;
        }
        String nickname = friend.getNickname();
        if ( nickname == null ) {
            return null;
        }
        return nickname;
    }

    private Long entityReporterId(Report report) {
        if ( report == null ) {
            return null;
        }
        Users reporter = report.getReporter();
        if ( reporter == null ) {
            return null;
        }
        Long id = reporter.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long entityReportedId(Report report) {
        if ( report == null ) {
            return null;
        }
        Users reported = report.getReported();
        if ( reported == null ) {
            return null;
        }
        Long id = reported.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
