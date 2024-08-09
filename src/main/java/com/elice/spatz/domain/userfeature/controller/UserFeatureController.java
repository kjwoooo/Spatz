package com.elice.spatz.domain.userfeature.controller;

import com.elice.spatz.config.CustomUserDetails;
import com.elice.spatz.domain.userfeature.dto.request.BlockCreateDto;
import com.elice.spatz.domain.userfeature.dto.request.FriendRequestCreateDto;
import com.elice.spatz.domain.userfeature.dto.request.ReportCreateDto;
import com.elice.spatz.domain.userfeature.dto.request.ReportUpdateDto;
import com.elice.spatz.domain.userfeature.dto.response.BlockDto;
import com.elice.spatz.domain.userfeature.dto.response.FriendDto;
import com.elice.spatz.domain.userfeature.dto.response.FriendRequestDto;
import com.elice.spatz.domain.userfeature.dto.response.ReportDto;
import com.elice.spatz.domain.userfeature.entity.ReportStatus;
import com.elice.spatz.domain.userfeature.entity.Status;
import com.elice.spatz.domain.userfeature.service.UserFeatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequiredArgsConstructor
public class UserFeatureController {
    private final UserFeatureService userFeatureService;

    // 1. 차단 요청
    @PostMapping("users/{userId}/block")
    public ResponseEntity<String> createBlock(@AuthenticationPrincipal CustomUserDetails loginUser, @PathVariable long userId){
        BlockCreateDto blockCreateDto = new BlockCreateDto(loginUser.getId(), userId);
        userFeatureService.createBlock(blockCreateDto);
        return ResponseEntity.ok("차단이 완료되었습니다.");
    }
    // 2. 차단 조회
    @GetMapping("/blocks")
    public ResponseEntity<Page<BlockDto>> getBlocks(@AuthenticationPrincipal CustomUserDetails loginUser , @PageableDefault(page=0, size=10, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        Page<BlockDto> blocks = userFeatureService.getBlocks(loginUser.getId(), pageable);
        return ResponseEntity.ok(blocks);
    }
    // 3. 차단 해제 (하드딜리트)
    @DeleteMapping("/blocks/{blockId}")
    public ResponseEntity<String> unBlock(@PathVariable long blockId){
        userFeatureService.unBlock(blockId);
        return ResponseEntity.ok("차단 해제가 완료되었습니다.");
    }

    // 1. 친구 요청
    @PostMapping("/users/{userId}/friend-request")
    public ResponseEntity<String> createFriendRequest(@AuthenticationPrincipal CustomUserDetails loginUser, @PathVariable long userId){
        FriendRequestCreateDto friendRequestCreateDto = new FriendRequestCreateDto(loginUser.getId(), userId, Status.WAITING);
        userFeatureService.createFriendRequest(friendRequestCreateDto);
        return ResponseEntity.ok("친구 요청이 완료되었습니다.");
    }
    // 2. 보낸/받은 요청 조회
    @GetMapping("/friend-requests")
    public ResponseEntity<Page<FriendRequestDto>> getSentFriendRequests(@AuthenticationPrincipal CustomUserDetails loginUser, @RequestParam String status, @PageableDefault(page=0, size=10, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        Page<FriendRequestDto> friendRequests = userFeatureService.getFriendRequests(status, loginUser.getId(), pageable);
        return ResponseEntity.ok(friendRequests);
    }
    // 3. 받은 요청 응답
    @PatchMapping("/friend-requests/{friendRequestId}")
    public ResponseEntity<String> responseReceivedFriendRequest(@PathVariable long friendRequestId, @RequestParam String status){
        userFeatureService.responseReceivedFriendRequest(friendRequestId, status);
        return ResponseEntity.ok("받은 요청에 대한 응답이 완료되었습니다.");
    }
    // 4. 보낸/받은 요청 삭제 (하드딜리트)
    @DeleteMapping("/friend-requests/{friendRequestId}")
    public ResponseEntity<String> deleteSentFriendRequest(@PathVariable long friendRequestId){
        userFeatureService.deleteSentFriendRequest(friendRequestId);
        return ResponseEntity.ok("보낸 요청이 삭제되었습니다.");
    }

    // 1. 친구 조회
    @GetMapping("/friendships")
    public ResponseEntity<Page<FriendDto>> getFriendships(@AuthenticationPrincipal CustomUserDetails loginUser, @PageableDefault(page=0, size=10, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        Page<FriendDto> friendDtos = userFeatureService.getFriendShips(loginUser.getId(), pageable);
        return ResponseEntity.ok(friendDtos);
    }
    // 2. 친구 검색 조회
    @GetMapping("/friendships/search")
    public ResponseEntity<Page<FriendDto>> searchFriendships(@AuthenticationPrincipal CustomUserDetails loginUser, @RequestParam String keyword, @PageableDefault(page=0, size=10, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<FriendDto> friendDtosByKeyword = userFeatureService.searchFriendships(keyword, loginUser.getId(), pageable);
        return ResponseEntity.ok(friendDtosByKeyword);
    }
    // 3. 친구 해제 (하드딜리트)
    @DeleteMapping("/friendships/{friendshipId}")
    public ResponseEntity<String> deleteFriendship(@PathVariable long friendshipId){
        userFeatureService.deleteFriendShip(friendshipId);
        return ResponseEntity.ok("친구 삭제가 완료되었습니다.");
    }

    // 1. 신고 요청
    @PostMapping("users/{userId}/report")
    public ResponseEntity<String> createReport(@AuthenticationPrincipal CustomUserDetails loginUser,
                                               @PathVariable long userId,
                                               @RequestParam("reportReason") String reportReason,
                                               @RequestParam("file") MultipartFile file) throws IOException{
        ReportCreateDto newReportCreateDto = new ReportCreateDto(loginUser.getId(), userId, reportReason);
        userFeatureService.createReport(newReportCreateDto, file);
        return ResponseEntity.ok("신고 요청이 완료되었습니다.");
    }
    // 2. 처리 전/후 신고 조회
    @GetMapping("reports")
    public ResponseEntity<Page<ReportDto>> getReports(@AuthenticationPrincipal CustomUserDetails loginUser,
                                                      @RequestParam ReportStatus reportStatus,
                                                      @PageableDefault(page=0, size=10, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        Page<ReportDto> reportDtos = userFeatureService.getReports(loginUser.getId(), reportStatus, pageable);
        return ResponseEntity.ok(reportDtos);
    }
    // 3. 신고 수정
    @PutMapping("reports/{reportId}")
    public ResponseEntity<String> updateReport(@PathVariable long reportId,
                                               @RequestParam("reportReason") String reportReason,
                                               @RequestParam("file") MultipartFile file) throws IOException{
        ReportUpdateDto reportUpdateDto = new ReportUpdateDto(reportId, reportReason);
        userFeatureService.updateReport(reportUpdateDto, reportId, file);
        return ResponseEntity.ok("신고 수정 완료되었습니다.");
    }
    // 4. 처리 전/후 신고 삭제
    @DeleteMapping("reports/{reportId}")
    public ResponseEntity<String> deleteReport(@PathVariable long reportId){
        userFeatureService.deleteReport(reportId);
        return ResponseEntity.ok("신고 삭제가 완료되었습니다.");
    }
}
