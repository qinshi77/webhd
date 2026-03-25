package com.example.webhd.controller;

import com.example.webhd.dto.CommentCreateDTO;
import com.example.webhd.service.ToolCommentService;
import com.example.webhd.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tools")
@CrossOrigin(origins = "http://localhost:8080")
public class ToolCommentController {

    private final ToolCommentService toolCommentService;
    private final UserService userService;

    public ToolCommentController(ToolCommentService toolCommentService,
                                 UserService userService) {
        this.toolCommentService = toolCommentService;
        this.userService = userService;
    }

    /**
     * 添加评论
     * POST /api/tools/{toolId}/comments
     */
    @PostMapping("/{toolId}/comments")
    public ResponseEntity<?> addComment(
            @RequestHeader(value = "User-ID", required = false) Integer userId,
            @PathVariable Integer toolId,
            @Valid @RequestBody CommentCreateDTO commentDTO) {

        // 验证登录
        if (userId == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "请先登录");
            return ResponseEntity.status(401).body(error);
        }

        try {
            Long userIdLong = userId.longValue();
            Map<String, Object> userInfo = userService.getUserInfo2(userIdLong);
            String username = (String) userInfo.get("username");
            String avatar = (String) userInfo.get("avatar");

            // 直接使用路径参数 toolId，不需要从 DTO 获取
            Map<String, Object> comment = toolCommentService.addComment(
                    toolId, userId, username, avatar, commentDTO.getContent());  // 修改方法签名

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "评论成功");
            response.put("data", comment);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 获取工具的评论列表
     * GET /api/tools/{toolId}/comments
     */
    @GetMapping("/{toolId}/comments")
    public ResponseEntity<?> getComments(@PathVariable Integer toolId) {
        try {
            Map<String, Object> result = toolCommentService.getCommentsByToolId(toolId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
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
     * 删除评论
     * DELETE /api/tools/comments/{commentId}
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(
            @RequestHeader(value = "User-ID", required = false) Integer userId,
            @PathVariable Integer commentId) {

        // 验证登录
        if (userId == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "请先登录");
            return ResponseEntity.status(401).body(error);
        }

        try {
            // 将 Integer 转换为 Long
            Long userIdLong = userId.longValue();

            // 获取用户名 - 使用 getUserInfo2 方法
            Map<String, Object> userInfo = userService.getUserInfo2(userIdLong);
            String username = (String) userInfo.get("username");

            boolean deleted = toolCommentService.deleteComment(commentId, username);

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
}