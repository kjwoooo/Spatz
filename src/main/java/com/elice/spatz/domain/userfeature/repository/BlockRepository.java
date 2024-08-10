package com.elice.spatz.domain.userfeature.repository;

import com.elice.spatz.domain.userfeature.entity.Block;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    // 차단한 사용자 목록
    Page<Block> findAllByBlockerIdAndBlockedBannedUserIsNull(Long id, Pageable pageable);
    Optional<Block> findByBlockerIdAndBlockedId(Long blockerId, Long blockedId);
}
