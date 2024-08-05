package com.elice.spatz.domain.userfeature.repository;

import com.elice.spatz.domain.userfeature.model.entity.FriendRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    // 보낸 요청 조회
    Page<FriendRequest> findAllByRequesterIdAndRecipientBannedUserIsNull(Long requesterId, Pageable pageable);
    // 받은 요청 조회
    Page<FriendRequest> findAllByRecipientIdAndRequesterBannedUserIsNull(Long recipientId, Pageable pageable);
}
