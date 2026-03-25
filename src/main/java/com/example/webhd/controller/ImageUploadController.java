package com.example.webhd.controller;

import com.example.webhd.dto.ImageUploadDTO;
import com.example.webhd.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "http://localhost:8080")
public class ImageUploadController {

    @Value("${image.upload.path}")
    private String uploadPath;

    @Value("${image.access.url}")
    private String accessUrl;

    /**
     * 上传单张图片
     */
    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 1. 上传文件
            String fileName = FileUploadUtil.uploadImage(file, uploadPath);

            // 2. 构建访问URL
            String imageUrl = accessUrl + fileName;

            // 3. 返回结果
            ImageUploadDTO result = new ImageUploadDTO();
            result.setUrl(imageUrl);
            result.setFileName(fileName);
            result.setSize(file.getSize());
            result.setMessage("上传成功");

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 上传多张图片
     */
    @PostMapping("/images")
    public ResponseEntity<?> uploadImages(@RequestParam("files") MultipartFile[] files) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", new java.util.ArrayList<>());

            for (MultipartFile file : files) {
                String fileName = FileUploadUtil.uploadImage(file, uploadPath);
                String imageUrl = accessUrl + fileName;

                ImageUploadDTO result = new ImageUploadDTO();
                result.setUrl(imageUrl);
                result.setFileName(fileName);
                result.setSize(file.getSize());

                ((java.util.List) response.get("data")).add(result);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}