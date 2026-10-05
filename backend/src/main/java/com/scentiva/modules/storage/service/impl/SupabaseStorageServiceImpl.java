package com.scentiva.modules.storage.service.impl;

import com.scentiva.modules.storage.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
public class SupabaseStorageServiceImpl implements StorageService {

    @Value("${scentiva.supabase.url:https://bpslvynnoegckkfjtsqo.supabase.co}")
    private String supabaseUrl;

    @Value("${scentiva.supabase.service-role-key:eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJwc2x2eW5ub2VnY2trZmp0c3FvIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc5MTE5ODM1NSwiZXhwIjoyMTA2Nzc0MzU1fQ.QKj2OKhwUMQced1mi7yClTq4JuqDF4benV9HgHip9cI}")
    private String serviceRoleKey;

    @Value("${scentiva.supabase.storage.bucket-name:scentiva-media}")
    private String bucketName;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload empty file");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String subFolder = (folder != null && !folder.isBlank()) ? folder.trim().replaceAll("^/|/$", "") + "/" : "";
        String uniqueFilename = subFolder + UUID.randomUUID() + extension;
        String uploadEndpoint = String.format("%s/storage/v1/object/%s/%s", supabaseUrl, bucketName, uniqueFilename);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(file.getContentType() != null ? file.getContentType() : "application/octet-stream"));
            headers.set("Authorization", "Bearer " + serviceRoleKey);
            headers.set("apikey", serviceRoleKey);
            headers.set("x-upsert", "true");

            HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);
            ResponseEntity<String> response = restTemplate.exchange(uploadEndpoint, HttpMethod.POST, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                String publicUrl = String.format("%s/storage/v1/object/public/%s/%s", supabaseUrl, bucketName, uniqueFilename);
                log.info("Successfully uploaded file to Supabase Storage: {}", publicUrl);
                return publicUrl;
            } else {
                throw new RuntimeException("Failed to upload file to Supabase Storage. HTTP Status: " + response.getStatusCode());
            }
        } catch (IOException e) {
            log.error("Failed to read file bytes for upload", e);
            throw new RuntimeException("Failed to read file bytes for upload: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error communicating with Supabase Storage", e);
            throw new RuntimeException("Supabase Storage error: " + e.getMessage(), e);
        }
    }
}
