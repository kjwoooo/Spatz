package com.elice.spatz.domain.userfeature.model.dto.request;

import com.elice.spatz.domain.userfeature.model.entity.Block;
import com.elice.spatz.domain.userfeature.model.entity.FriendRequest;
import com.elice.spatz.domain.userfeature.model.entity.Report;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-08-05T20:17:52+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class RequestMapperImpl implements RequestMapper {

    @Override
    public Block blockCreateDtoToBlock(BlockCreateDto dto) {
        if ( dto == null ) {
            return null;
        }

        Block block = new Block();

        block.setBlocker( idToUser( dto.getBlockerId() ) );
        block.setBlocked( idToUser( dto.getBlockedId() ) );

        return block;
    }

    @Override
    public FriendRequest friendRequestCreateDtoToFriendRequest(FriendRequestCreateDto dto) {
        if ( dto == null ) {
            return null;
        }

        FriendRequest friendRequest = new FriendRequest();

        friendRequest.setRequester( idToUser( dto.getRequesterId() ) );
        friendRequest.setRecipient( idToUser( dto.getRecipientId() ) );
        friendRequest.setRequestStatus( dto.getRequestStatus() );

        return friendRequest;
    }

    @Override
    public Report reportCreateDtoToReport(ReportCreateDto dto) {
        if ( dto == null ) {
            return null;
        }

        Report report = new Report();

        report.setReporter( idToUser( dto.getReporterId() ) );
        report.setReported( idToUser( dto.getReportedId() ) );
        report.setReportReason( dto.getReportReason() );

        return report;
    }

    @Override
    public Report reportUpdateDtoToReport(ReportUpdateDto dto) {
        if ( dto == null ) {
            return null;
        }

        Report report = new Report();

        report.setId( (int) dto.getId() );
        report.setReporter( idToUser( dto.getReporterId() ) );
        report.setReported( idToUser( dto.getReportedId() ) );
        report.setReportReason( dto.getReportReason() );

        return report;
    }
}
