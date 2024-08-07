package com.elice.spatz.domain.userfeature.repository;

import com.elice.spatz.domain.userfeature.entity.Friendship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    // 내가 userId인 친구 조회
    Optional<Friendship> findByUserIdAndFriendId(Long userId, Long friendId);
    Page<Friendship> findAllByUserIdAndFriendBannedUserIsNull(Long userId, Pageable pageable);

    // 내가 friendId인 친구 조회
    Optional<Friendship> findByFriendIdAndUserId(Long friendId, Long userId);
    Page<Friendship> findAllByFriendIdAndUserBannedUserIsNull(Long friendId, Pageable pageable);
}
