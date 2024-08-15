package com.elice.spatz.domain.server.repository;

import com.elice.spatz.domain.server.entity.Servers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface ServerRepository extends JpaRepository<Servers, Long>  {
    Optional<Servers> findByInviteCode(String inviteCode);
}
