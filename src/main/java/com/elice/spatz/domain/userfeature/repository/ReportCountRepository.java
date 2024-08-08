package com.elice.spatz.domain.userfeature.repository;

import com.elice.spatz.domain.userfeature.entity.ReportCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportCountRepository extends JpaRepository<ReportCount, Long> {
    ReportCount findByUserId(Long userId);
}
