package com.bookmall.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bookmall.service.impl.ImgBBService;

@RestController
@RequestMapping("/api/images")
public class ImageUploadController {

    @Autowired
    private ImgBBService imgBBService;

    // 定義 5MB 的常數 (5 * 1024 * 1024 bytes)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("image") MultipartFile file) {
        // 1. 檔案大小驗證 (第一原理：在執行昂貴操作前先過濾無效請求)
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "請選擇檔案"));
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return ResponseEntity.badRequest().body(Map.of("message", "檔案大小不能超過 5MB"));
        }

        // 2. 呼叫 Service 執行上傳
        try {
            String imageUrl = imgBBService.uploadImage(file);
            return ResponseEntity.ok(Map.of("url", imageUrl));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "圖片上傳失敗：" + e.getMessage()));
        }
    }
}