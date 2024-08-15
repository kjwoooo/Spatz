package com.elice.spatz.domain.serverUser.repository;

import com.elice.spatz.domain.server.entity.Servers;
import com.elice.spatz.domain.serverUser.entity.ServerUser;
import com.elice.spatz.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ServerUserRepository extends JpaRepository<ServerUser, Long> {
    // 특정 사용자가 속한 서버 목록을 반환하는 메서드
    @Query("SELECT su.server FROM ServerUser su WHERE su.user = :user")
    List<Servers> findServersByUser(Users user);

}
