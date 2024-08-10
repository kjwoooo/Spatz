package com.elice.spatz.domain.userfeature.service;

import com.elice.spatz.domain.user.entity.Users;
import com.elice.spatz.domain.user.repository.UserRepository;
import com.elice.spatz.domain.userfeature.dto.request.*;
import com.elice.spatz.domain.userfeature.dto.response.*;
import com.elice.spatz.domain.userfeature.entity.*;
import com.elice.spatz.domain.userfeature.repository.*;
import com.elice.spatz.exception.errorCode.UserFeatureErrorCode;
import com.elice.spatz.exception.exception.UserFeatureException;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.elice.spatz.domain.userfeature.entity.Status.*;
import static java.lang.Boolean.TRUE;

@Service
@RequiredArgsConstructor
public class UserFeatureService {
    private final BlockRepository blockRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;
    private final ReportRepository reportRepository;
    private final RequestMapper requestMapper;
    private final ResponseMapper responseMapper;
    private final UserRepository userRepository;
    private final BannedUserRepository bannedUserRepository;

    // 공통 예외 처리 메소드 - 사용자 존재 여부 확인
    private void CheckUserExists(Long userId){
        userRepository.findById(userId).orElseThrow(()->
                new UserFeatureException(UserFeatureErrorCode.NOT_FOUND_USER)
        );
    }

    // 1. 차단 요청
    @Transactional
    public void createBlock(BlockCreateDto blockCreateDto){
        // 예외 체크: 차단할 사용자 존재하는지 확인 -> 이미 차단 관계인지 확인 -> 차단 대상이 자신인지 확인
        Long blockerId = blockCreateDto.getBlockerId();
        Long blockedId = blockCreateDto.getBlockedId();
        CheckUserExists(blockedId);
        blockRepository.findByBlockerIdAndBlockedId(blockerId, blockedId).ifPresent(block -> {
            throw new UserFeatureException(UserFeatureErrorCode.ALREADY_BLOCKED);
        });
        if (blockerId.equals(blockedId)){
            throw new UserFeatureException(UserFeatureErrorCode.BLOCK_USER_SELF);
        }

        Block newBlock = requestMapper.blockCreateDtoToBlock(blockCreateDto);
        blockRepository.save(newBlock);
    }
    // 2. 차단 조회
    @Transactional
    public Page<BlockDto> getBlocks(Long blockerId, Pageable pageable){
        Page<Block> blocks = blockRepository.findAllByBlockerIdAndBlockedBannedUserIsNull(blockerId, pageable);
        List<BlockDto> blockDtoList = new ArrayList<>();
        blockDtoList = blocks.getContent().stream()
                .map(responseMapper::blockToBlockDto)
                .collect(Collectors.toList());

        return new PageImpl<>(blockDtoList, pageable, blocks.getTotalElements());
    }
    // 3. 차단 해제 (하드딜리트)
    @Transactional
    public void unBlock(long id) {
        // 예외 체크: 이미 차단 해제된 상태인지 확인
        if(!blockRepository.existsById(id)){
            throw new UserFeatureException(UserFeatureErrorCode.ALREADY_UNBLOCKED);
        }

        blockRepository.deleteById(id);
    }

    // 1. 친구 요청
    @Transactional
    public void createFriendRequest(FriendRequestCreateDto friendRequestCreateDto){
        // 예외 체크 1: 요청할 사용자 존재하는지 확인 -> 요청 대상이 자신인지 확인 -> 친구 관계인지 확인 -> 정지된 사용자인지 확인 -> 차단 관계인지 확인
        long requesterId = friendRequestCreateDto.getRequesterId();
        long recipientId = friendRequestCreateDto.getRecipientId();
        CheckUserExists(recipientId);
        if (requesterId == recipientId){
            throw new UserFeatureException(UserFeatureErrorCode.REQUEST_USER_SELF);
        }
        friendshipRepository.findByUserIdAndFriendId(requesterId, recipientId).ifPresent((firend)->{
                throw new UserFeatureException(UserFeatureErrorCode.ALREADY_FRIEND);}
        );
        friendshipRepository.findByUserIdAndFriendId(requesterId, recipientId).ifPresent((friend)->{
                throw new UserFeatureException(UserFeatureErrorCode.ALREADY_FRIEND);}
        );
        bannedUserRepository.findByUserId(recipientId).ifPresent((bannedUser)->{
            throw new UserFeatureException(UserFeatureErrorCode.BANNED_USER);}
        );
        blockRepository.findByBlockerIdAndBlockedId(requesterId, recipientId).ifPresent((block)-> {
            throw new UserFeatureException(UserFeatureErrorCode.BLOCKED_USER);
        });
        blockRepository.findByBlockerIdAndBlockedId(recipientId, requesterId).ifPresent((block)-> {
            throw new UserFeatureException(UserFeatureErrorCode.BLOCKED_USER);
        });

        // 예외 체크 2: 이미 친구 요청을 전송한 상태인지 확인 -> 상대방에게 친구 요청을 받은 상태인지 확인
        friendRequestRepository.findByRequesterIdAndRecipientIdAndRequestStatus(requesterId, recipientId, WAITING).ifPresent((friendRequest)-> {
                throw new UserFeatureException(UserFeatureErrorCode.ALREADY_REQUESTED);}
        );
        friendRequestRepository.findByRecipientIdAndRequesterIdAndRequestStatus(recipientId, requesterId, WAITING).ifPresent((friendRequest)-> {
            throw new UserFeatureException(UserFeatureErrorCode.ALREADY_RECEIVED);}
        );

        FriendRequest friendRequest = requestMapper.friendRequestCreateDtoToFriendRequest(friendRequestCreateDto);
        friendRequestRepository.save(friendRequest);
    }
    // 2. 보낸/받은 요청 조회
    @Transactional
    public Page<FriendRequestDto> getFriendRequests(String status, Long userId, Pageable pageable){
        Page<FriendRequest> friendRequests;

        if (status.equals("sent")){
            friendRequests = friendRequestRepository.findAllByRequesterIdAndRecipientBannedUserIsNull(userId, pageable);
        } else if (status.equals("received")){
            friendRequests = friendRequestRepository.findAllByRecipientIdAndRequestStatusAndRequesterBannedUserIsNull(userId, WAITING, pageable);
        } else {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        List<FriendRequestDto> friendRequestDtoList = friendRequests.getContent().stream()
                .map(responseMapper::friendRequestToFriendRequestDto)
                .collect(Collectors.toList());

        return new PageImpl<>(friendRequestDtoList, pageable, friendRequests.getTotalElements());
    }
    // 3. 받은 요청 응답
    @Transactional
    public void responseReceivedFriendRequest(Long id, String status){
        // 예외 체크: 이미 응답한 요청인지 확인
        List<Status> statuses = Arrays.asList(Status.ACCEPTED, Status.REJECTED);
        friendRequestRepository.findByIdAndRequestStatusIn(id, statuses).ifPresent(friendRequest -> {
                    throw new UserFeatureException(UserFeatureErrorCode.ALREADY_RESPONSE);
        });

        FriendRequest receivedFriendRequest = friendRequestRepository.findById(id).orElseThrow();
        if(status.equals("ACCEPTED")){
            receivedFriendRequest.setRequestStatus(ACCEPTED);
            Users requester = receivedFriendRequest.getRequester();
            Users recipient = receivedFriendRequest.getRecipient();

            Friendship friendship = new Friendship();
            friendship.setUser(requester);
            friendship.setFriend((recipient));
            friendship.setFriendStatus(TRUE);

            friendshipRepository.save(friendship);
        } else if (status.equals("REJECTED")){
            receivedFriendRequest.setRequestStatus(REJECTED);
        }
        else {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        friendRequestRepository.save(receivedFriendRequest);
    }
    // 4. 보낸/받은 요청 삭제 (하드딜리트)
    @Transactional
    public void deleteSentFriendRequest(Long id){
        friendRequestRepository.deleteById(id);
    }

    // 1. 친구 조회
    @Transactional
    public Page<FriendDto> getFriendShips(Long userId, Pageable pageable){
        // 내가 userId로 등록된 친구 목록 조회
        Page<Friendship> friendshipsAsUser = friendshipRepository.findAllByUserIdAndFriendBannedUserIsNull(userId, pageable);
        List<FriendDto> userFriends = friendshipsAsUser.getContent().stream()
                .map(f -> new FriendDto(
                        f.getId(),
                        f.getUser().getId(),
                        f.getFriend().getId(),
                        f.getUser().getNickname(),
                        f.getFriend().getNickname()))
                .collect(Collectors.toList());

        // 내가 friendId로 등록된 친구 조회
        Page<Friendship> friendshipsAsFriend = friendshipRepository.findAllByFriendIdAndUserBannedUserIsNull(userId, pageable);
        List<FriendDto> friendUsers = friendshipsAsFriend.getContent().stream()
                .map(f -> new FriendDto(
                        f.getId(),
                        f.getFriend().getId(),
                        f.getUser().getId(),
                        f.getFriend().getNickname(),
                        f.getUser().getNickname()))
                .collect(Collectors.toList());

        // 최종 친구 목록
        List<FriendDto> allFriendDtos = new ArrayList<>();
        allFriendDtos.addAll(userFriends);
        allFriendDtos.addAll(friendUsers);

        // 페이지 처리
        long totalElements = allFriendDtos.size();
        return new PageImpl<>(allFriendDtos, pageable, totalElements);
    }
    // 2. 친구 검색 조회
    @Transactional
    public Page<FriendDto> searchFriendships(String keyword, Long userId, Pageable pageable){
        // 내가 userId로 등록된 친구 목록 조회
        Page<Friendship> friendshipsAsUser = friendshipRepository.findAllByUserIdAndFriendBannedUserIsNull(userId, pageable);
        List<FriendDto> userFriends = friendshipsAsUser.getContent().stream()
                .map(f -> new FriendDto(
                        f.getId(),
                        f.getUser().getId(),
                        f.getFriend().getId(),
                        f.getUser().getNickname(),
                        f.getFriend().getNickname()))
                .filter(dto -> dto.getFriendNickname().contains(keyword))
                .collect(Collectors.toList());

        // 내가 friendId로 등록된 친구 조회
        Page<Friendship> friendshipsAsFriend = friendshipRepository.findAllByFriendIdAndUserBannedUserIsNull(userId, pageable);
        List<FriendDto> friendUsers = friendshipsAsFriend.getContent().stream()
                .map(f -> new FriendDto(
                        f.getId(),
                        f.getFriend().getId(),
                        f.getUser().getId(),
                        f.getFriend().getNickname(),
                        f.getUser().getNickname()))
                .filter(dto -> dto.getUserNickname().contains(keyword))
                .collect(Collectors.toList());

        // 최종 친구 목록
        List<FriendDto> allFriendDtos = new ArrayList<>();
        allFriendDtos.addAll(userFriends);
        allFriendDtos.addAll(friendUsers);

        // 페이지 처리
        long totalElements = allFriendDtos.size();
        return new PageImpl<>(allFriendDtos, pageable, totalElements);
    }
    // 3. 친구 해제 (하드딜리트)
    @Transactional
    public void deleteFriendShip(Long id){
        // 예외 체크: 이미 친구 해제된 상태인지 확인
        if(!friendshipRepository.existsById(id)){
            throw new UserFeatureException(UserFeatureErrorCode.ALREADY_UNFRIENDED);
        }

        friendshipRepository.deleteById(id);
    }

    // 1. 신고 요청
    @Transactional
    public void createReport(ReportCreateDto reportCreateDto, MultipartFile file)throws IOException {
        // 예외 체크: 신고할 사용자 존재하는지 확인 -> 신고한 사용자가 자신인지 확인 -> 이미 전송된 신고가 처리되지 않은 상태인지 확인
        long reporterId = reportCreateDto.getReporterId();
        long reportedId = reportCreateDto.getReportedId();
        CheckUserExists(reportedId);
        if (reporterId == reportedId){
            throw new UserFeatureException(UserFeatureErrorCode.REPORT_USER_SELF);
        }
        reportRepository.findByReporterIdAndReportedIdAndReportStatus(reporterId, reportedId, ReportStatus.WAITING).ifPresent((report)-> {
                throw new UserFeatureException(UserFeatureErrorCode.ALREADY_REPORTED);}
        );

        byte[] imageBytes = file.getBytes();
        Report newReport = requestMapper.reportCreateDtoToReport(reportCreateDto);
        newReport.setReportImage(imageBytes);
        newReport.setReportStatus(ReportStatus.WAITING);
        reportRepository.save(newReport);
    }
    // 2. 처리 전/후 신고 조회
    @Transactional
    public Page<ReportDto> getReports(Long reporterId, ReportStatus reportStatus, Pageable pageable){
        Page<Report> reports;
        if (reportStatus.equals(ReportStatus.WAITING)) {
            reports = reportRepository.findAllByReporterIdAndReportStatus(reporterId, reportStatus, pageable);
        }
        else {
            reports = reportRepository.findAllByReporterIdAndReportStatusNot(reporterId, ReportStatus.WAITING, pageable);
        }

        List<ReportDto> reportDtoList = reports.getContent().stream()
                .map(responseMapper::reportToReportDto)
                .collect(Collectors.toList());
        return new PageImpl<>(reportDtoList, pageable, reports.getTotalElements());
    }
    // 3. 신고 상세 조회
    @Transactional
    public ReportDto getDetailReport(Long id){
        // 예외 체크: 조회할 신고 정보가 존재하는지 확인
        Report report = reportRepository.findById(id).orElseThrow(()->
                new UserFeatureException(UserFeatureErrorCode.NOT_FOUND_REPORT)
        );

        return responseMapper.reportToReportDto(report);
    }
    // 4. 신고 수정
    @Transactional
    public void updateReport(ReportUpdateDto reportUpdateDto, Long id, MultipartFile file)throws IOException {
        // 예외 체크: 수정할 신고 정보가 존재하는지 확인
        Report existingReport = reportRepository.findById(id).orElseThrow(() ->
                new UserFeatureException(UserFeatureErrorCode.NOT_FOUND_REPORT)
        );

        existingReport.setReportReason(reportUpdateDto.getReportReason());
        if (file != null && !file.isEmpty()) {
            byte[] imageBytes = file.getBytes();
            existingReport.setReportImage(imageBytes);
        }

        reportRepository.save(existingReport);
    }
    // 5. 처리 전/후 신고 삭제
    @Transactional
    public void deleteReport(Long reportId){
        // 예외 체크: 신고가 이미 철회/삭제되었는지 확인
        if(!reportRepository.existsById(reportId)){
            throw new UserFeatureException(UserFeatureErrorCode.ALREADY_DELETED);
        }

        reportRepository.deleteById(reportId);
    }
}