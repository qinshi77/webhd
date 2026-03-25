package com.example.webhd.controller;

import com.example.webhd.dto.FollowDTO;
import com.example.webhd.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/follow")
@CrossOrigin(origins = "http://localhost:8080")
public class FollowController {

    @Autowired
    private FollowService followService;

    /**
     * 关注用户
     */
    @PostMapping
    public ResponseEntity<?> follow(
            @RequestHeader(value = "User-ID", required = false) Long userId,
            @Valid @RequestBody FollowDTO followDTO) {

        // 验证登录
        if (userId == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "请先登录");
            return ResponseEntity.status(401).body(error);
        }

        try {
            Map<String, Object> result = followService.followUser(userId, followDTO.getFollowingId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "关注成功");
            response.put("data", result);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 取消关注
     */
    @DeleteMapping("/{followingId}")
    public ResponseEntity<?> unfollow(
            @RequestHeader(value = "User-ID", required = false) Long userId,
            @PathVariable Long followingId) {

        // 验证登录
        if (userId == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "请先登录");
            return ResponseEntity.status(401).body(error);
        }

        try {
            Map<String, Object> result = followService.unfollowUser(userId, followingId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "取消关注成功");
            response.put("data", result);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 检查是否已关注
     */
    @GetMapping("/check/{followingId}")
    public ResponseEntity<?> checkFollow(
            @RequestHeader(value = "User-ID", required = false) Long userId,
            @PathVariable Long followingId) {

        boolean isFollowing = followService.isFollowing(userId, followingId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("isFollowing", isFollowing);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户的关注列表
     */
    @GetMapping("/following/{userId}")
    public ResponseEntity<?> getFollowingList(
            @PathVariable Long userId,
            @RequestHeader(value = "User-ID", required = false) Long currentUserId) {

        try {
            Map<String, Object> result = followService.getFollowingList(userId, currentUserId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 获取用户的粉丝列表
     */
    @GetMapping("/followers/{userId}")
    public ResponseEntity<?> getFollowersList(
            @PathVariable Long userId,
            @RequestHeader(value = "User-ID", required = false) Long currentUserId) {

        try {
            Map<String, Object> result = followService.getFollowersList(userId, currentUserId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 获取用户的统计数据（关注数、粉丝数）
     */
    @GetMapping("/stats/{userId}")
    public ResponseEntity<?> getUserStats(@PathVariable Long userId) {
        try {
            Map<String, Object> stats = followService.getUserStats(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}