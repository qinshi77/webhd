package com.example.webhd.controller;

import com.example.webhd.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "http://localhost:8080")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 添加评论
     * POST /api/comments
     * {
     *     "postId": 1,
     *     "content": "评论内容"
     * }
     */
    @PostMapping
    public ResponseEntity<?> addComment(
            @RequestHeader(value = "User-ID", required = false) Long userId,
            @RequestBody Map<String, Object> request) {

        // 验证用户是否登录
        if (userId == null) {
            return ResponseEntity.status(401).body("请先登录");
        }

        try {
            Long postId = Long.valueOf(request.get("postId").toString());
            String content = (String) request.get("content");

            if (content == null || content.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("评论内容不能为空");
            }

            Map<String, Object> comment = commentService.addComment(postId, userId, content);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "评论成功");
            response.put("comment", comment);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除评论
     * DELETE /api/comments/{commentId}
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
            @RequestHeader(value = "User-ID", required = false) Long userId,
            @PathVariable Long commentId) {

        if (userId == null) {
            return ResponseEntity.status(401).body("请先登录");
        }

        boolean deleted = commentService.deleteComment(commentId, userId);

        Map<String, Object> response = new HashMap<>();
        if (deleted) {
            response.put("success", true);
            response.put("message", "删除成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "删除失败，评论不存在或无权限");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取帖子的评论列表
     * GET /api/comments/post/{postId}
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<?> getPostComments(@PathVariable Long postId) {
        List<Map<String, Object>> comments = commentService.getPostComments(postId);
        return ResponseEntity.ok(comments);
    }
}