package com.example.webhd.controller;

import com.example.webhd.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/likes")
@CrossOrigin(origins = "http://localhost:8080")
public class LikeController {

    @Autowired
    private LikeService likeService;

    /**
     * 点赞
     * POST /api/likes/post/{postId}
     */
    @PostMapping("/post/{postId}")
    public ResponseEntity<?> likePost(
            @RequestHeader(value = "User-ID", required = false) Long userId,
            @PathVariable Long postId) {

        if (userId == null) {
            return ResponseEntity.status(401).body("请先登录");
        }

        try {
            Map<String, Object> result = likeService.likePost(postId, userId);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 取消点赞
     * DELETE /api/likes/post/{postId}
     */
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<?> unlikePost(
            @RequestHeader(value = "User-ID", required = false) Long userId,
            @PathVariable Long postId) {

        if (userId == null) {
            return ResponseEntity.status(401).body("请先登录");
        }

        Map<String, Object> result = likeService.unlikePost(postId, userId);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取帖子点赞状态
     * GET /api/likes/post/{postId}/status
     */
    @GetMapping("/post/{postId}/status")
    public ResponseEntity<?> getLikeStatus(
            @RequestHeader(value = "User-ID", required = false) Long userId,
            @PathVariable Long postId) {

        Map<String, Object> result = likeService.getLikeStatus(postId, userId);
        return ResponseEntity.ok(result);
    }
}