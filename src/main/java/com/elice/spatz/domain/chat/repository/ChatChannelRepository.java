package com.elice.spatz.domain.chat.repository;

import com.elice.spatz.domain.chat.entity.ChatChannel;
import com.elice.spatz.domain.server.entity.Servers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatChannelRepository extends JpaRepository<ChatChannel, Long> {
    List<ChatChannel> findAllByServerId(Long serverId);
    Optional<ChatChannel> findById(Long id);


}
