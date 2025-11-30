package com.example.airbnbproject.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3ImageStorageService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.base-url}")
    private String baseUrl;

    public String uploadAccommodationImage(Long accommodationId, MultipartFile file) throws IOException {
        String ext = extractExtension(file.getOriginalFilename());
        String key = "accommodation/" + accommodationId + "/" + UUID.randomUUID() + ext;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(request,
                software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));

        return baseUrl + "/" + URLEncoder.encode(key, StandardCharsets.UTF_8);
    }

    private String extractExtension(String originalFilename) {
        if (originalFilename == null) {
            return "";          // 확장자 없으면 그냥 빈 문자열
        }
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex == -1) {
            return "";          // . 이 없으면 확장자 없음
        }
        return originalFilename.substring(dotIndex); // 예: ".jpg", ".png"
    }
}

