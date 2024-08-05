package com.elice.spatz.domain.userfeature.repository;

import com.elice.spatz.domain.userfeature.model.entity.BannedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BannedUserRepository extends JpaRepository<BannedUser, Long> {
    List<BannedUser> findAllByBannedEnd(LocalDateTime bannedEnd);
}
