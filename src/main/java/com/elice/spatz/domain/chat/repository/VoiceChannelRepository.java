package com.elice.spatz.domain.chat.repository;

import com.elice.spatz.domain.chat.entity.ChatChannel;
import com.elice.spatz.domain.chat.entity.VoiceChannel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoiceChannelRepository extends JpaRepository<VoiceChannel, Long> {
    List<VoiceChannel> findAllByServerId(Long serverId);
}