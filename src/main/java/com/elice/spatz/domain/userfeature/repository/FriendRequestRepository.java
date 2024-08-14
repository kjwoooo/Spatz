package com.elice.spatz.domain.userfeature.repository;

import com.elice.spatz.domain.userfeature.entity.FriendRequest;
import com.elice.spatz.domain.userfeature.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    // 보낸 요청 조회
    Page<FriendRequest> findAllByRequesterIdAndRecipientBannedUserIsNull(Long requesterId, Pageable pageable);
    Optional<FriendRequest> findByRequesterIdAndRecipientIdAndRequestStatus(Long requesterId, Long recipientId, Status status);

    // 받은 요청 조회
    Page<FriendRequest> findAllByRecipientIdAndRequestStatusAndRequesterBannedUserIsNull(Long recipientId, Status status, Pageable pageable);
    Optional<FriendRequest> findByRecipientIdAndRequesterIdAndRequestStatus(Long recipientId, Long requesterId, Status status);

    // 응답하지 않은 요청 조회
    Optional<FriendRequest> findByIdAndRequestStatusIn(Long id, List<Status> statuses);

    // 삭제
    void deleteByRequesterIdAndRecipientId(Long requesterId, Long recipientId);
}
