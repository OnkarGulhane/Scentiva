package com.scentiva.modules.storage.controller;

import com.scentiva.common.response.ApiResponse;
import com.scentiva.modules.storage.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
@Tag(name = "Media & Storage", description = "Endpoints for uploading media assets to Supabase Cloud Storage")
public class MediaUploadController {

    private final StorageService storageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload image or media to Supabase Storage", description = "Uploads an image file to Supabase storage bucket and returns its public CDN URL")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadMedia(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", required = false, defaultValue = "products") String folder) {

        String publicUrl = storageService.uploadFile(file, folder);

        return ResponseEntity.ok(ApiResponse.<Map<String, String>>builder()
                .success(true)
                .statusCode(200)
                .message("File uploaded successfully to Supabase Storage")
                .data(Map.of("url", publicUrl, "filename", file.getOriginalFilename() != null ? file.getOriginalFilename() : "asset"))
                .build());
    }
}
