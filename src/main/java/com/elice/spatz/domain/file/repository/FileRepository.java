package com.elice.spatz.domain.file.repository;

import com.elice.spatz.domain.file.entity.File;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {

    @Transactional
    void deleteByStorageUrl(String storageUrl);
}
