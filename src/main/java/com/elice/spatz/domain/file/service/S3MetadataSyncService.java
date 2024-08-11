package com.elice.spatz.domain.file.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.elice.spatz.domain.file.entity.File;
import com.elice.spatz.domain.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class S3MetadataSyncService {

    private final AmazonS3 s3Client;
    private final FileRepository fileRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public void syncMetadata() {
        ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName);
        ListObjectsV2Result result;

        do {
            result = s3Client.listObjectsV2(req);

            List<S3ObjectSummary> objects = result.getObjectSummaries();
            for (S3ObjectSummary os : objects) {
                String fileName = os.getKey();
                String storageUrl = s3Client.getUrl(bucketName, fileName).toString();

                // 이미 저장된 파일 메타데이터가 있는지 확인
                if (!fileRepository.existsByStorageUrl(storageUrl)) {
                    File file = new File();
                    file.setFileName(fileName.substring(fileName.indexOf('_') + 1));
                    file.setFileKey(fileName);
                    file.setStorageUrl(storageUrl);

                    fileRepository.save(file);
                }
            }
            req.setContinuationToken(result.getNextContinuationToken());
        } while (result.isTruncated());
    }
}
