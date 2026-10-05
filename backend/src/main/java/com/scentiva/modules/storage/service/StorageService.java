package com.scentiva.modules.storage.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    /**
     * Uploads a media file (product image, banner, story cover) to Supabase Storage.
     * @param file The multipart file to upload
     * @param folder The target subfolder (e.g. "products", "banners", "stories")
     * @return The public CDN URL of the uploaded asset
     */
    String uploadFile(MultipartFile file, String folder);
}
