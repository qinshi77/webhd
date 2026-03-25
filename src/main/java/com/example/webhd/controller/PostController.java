package com.example.webhd.controller;

import com.example.webhd.dto.PostCreateDTO;
import com.example.webhd.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:8080")
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * 获取所有帖子
     * @param userId 当前登录用户ID（从Header获取，可选）
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllPosts(
            @RequestHeader(value = "User-ID", required = false) Long userId) {
        List<Map<String, Object>> posts = postService.getPostList(userId);
        return ResponseEntity.ok(posts);
    }

    /**
     * 获取帖子详情
     * @param postId 帖子ID
     * @param userId 当前登录用户ID
     */
    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostDetail(
            @PathVariable Long postId,
            @RequestHeader(value = "User-ID", required = false) Long userId) {
        try {
            Map<String, Object> post = postService.getPostDetail(postId, userId);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 发布新文章
     * @param userId 当前登录用户ID
     * @param postDTO 文章数据
     */
    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestHeader(value = "User-ID", required = false) Long userId,
            @Valid @RequestBody PostCreateDTO postDTO) {

        // 验证登录
        if (userId == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "请先登录");
            return ResponseEntity.status(401).body(error);
        }

        try {
            Map<String, Object> newPost = postService.createPost(postDTO, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "发布成功");
            response.put("post", newPost);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 删除帖子
     * @param postId 帖子ID
     * @param userId 当前登录用户ID
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(
            @RequestHeader(value = "User-ID", required = false) Long userId,
            @PathVariable Long postId) {

        // 验证登录
        if (userId == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "请先登录");
            return ResponseEntity.status(401).body(error);
        }

        try {
            boolean deleted = postService.deletePost(postId, userId);

            Map<String, Object> response = new HashMap<>();
            if (deleted) {
                response.put("success", true);
                response.put("message", "删除成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "删除失败");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 更新帖子
     * @param postId 帖子ID
     * @param userId 当前登录用户ID
     * @param postDTO 更新数据
     */
    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost(
            @RequestHeader(value = "User-ID", required = false) Long userId,
            @PathVariable Long postId,
            @RequestBody PostCreateDTO postDTO) {

        // 验证登录
        if (userId == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "请先登录");
            return ResponseEntity.status(401).body(error);
        }

        try {
            boolean updated = postService.updatePost(postId, userId,
                    postDTO.getContent(), postDTO.getImage());

            Map<String, Object> response = new HashMap<>();
            if (updated) {
                response.put("success", true);
                response.put("message", "更新成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "更新失败");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 获取用户的所有帖子
     * @param targetUserId 目标用户ID
     * @param userId 当前登录用户ID
     */
    @GetMapping("/user/{targetUserId}")
    public ResponseEntity<?> getUserPosts(
            @PathVariable Long targetUserId,
            @RequestHeader(value = "User-ID", required = false) Long userId) {

        try {
            List<Map<String, Object>> posts = postService.getUserPosts(targetUserId, userId);
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 搜索帖子
     * @param keyword 搜索关键词
     * @param userId 当前登录用户ID
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchPosts(
            @RequestParam String keyword,
            @RequestHeader(value = "User-ID", required = false) Long userId) {

        try {
            List<Map<String, Object>> posts = postService.searchPosts(keyword, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("keyword", keyword);
            response.put("count", posts.size());
            response.put("posts", posts);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 获取热门帖子
     * @param limit 数量限制（默认10）
     * @param userId 当前登录用户ID
     */
    @GetMapping("/hot")
    public ResponseEntity<?> getHotPosts(
            @RequestParam(defaultValue = "10") int limit,
            @RequestHeader(value = "User-ID", required = false) Long userId) {

        try {
            List<Map<String, Object>> posts = postService.getHotPosts(limit, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", posts.size());
            response.put("posts", posts);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 分页获取帖子
     * @param page 页码（默认0）
     * @param size 每页大小（默认10）
     * @param userId 当前登录用户ID
     */
    @GetMapping("/page")
    public ResponseEntity<?> getPostsByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(value = "User-ID", required = false) Long userId) {

        try {
            Map<String, Object> result = postService.getPostsByPage(page, size, userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 获取帖子总数
     */
    @GetMapping("/count")
    public ResponseEntity<?> getPostCount() {
        try {
            int count = postService.getPostCount();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", count);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}