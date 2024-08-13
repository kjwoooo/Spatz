package com.elice.spatz.config;

import com.elice.spatz.domain.file.service.S3MetadataSyncService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3MetadataSyncConfig {

    @Bean
    public CommandLineRunner syncS3Metadata(S3MetadataSyncService s3MetadataSyncService) {
        return args -> s3MetadataSyncService.syncMetadata();
    }
}
