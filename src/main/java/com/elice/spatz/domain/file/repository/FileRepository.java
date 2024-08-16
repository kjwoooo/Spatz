package com.elice.spatz.domain.file.repository;

import com.elice.spatz.domain.chat.entity.ChatChannel;
import com.elice.spatz.domain.file.entity.File;
import com.elice.spatz.domain.user.entity.Users;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {

    @Transactional
    void deleteByStorageUrl(String storageUrl);

    boolean existsByStorageUrl(String storageUrl);

    @Transactional
    List<File> findByChannel(ChatChannel channel);

    @Transactional
    List<File> findByUser(Users user);

    List<File> findByMessageId(String messageId);
}
