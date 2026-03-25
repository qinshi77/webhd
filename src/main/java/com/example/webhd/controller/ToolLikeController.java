package com.example.webhd.controller;

import com.example.webhd.service.ToolLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tools")
@CrossOrigin(origins = "*")
public class ToolLikeController {

    @Autowired
    private ToolLikeService toolLikeService;

    /**
     * 点赞
     * POST /api/tools/{toolId}/like
     */
    @PostMapping("/{toolId}/like")
    public ResponseEntity<?> likeTool(
            @RequestHeader(value = "User-ID", required = false) Integer userId,
            @PathVariable Integer toolId) {

        // 验证登录
        if (userId == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "请先登录");
            return ResponseEntity.status(401).body(error);
        }

        try {
            Map<String, Object> result = toolLikeService.likeTool(toolId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "点赞成功");
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
     * 取消点赞
     * DELETE /api/tools/{toolId}/like
     */
    @DeleteMapping("/{toolId}/like")
    public ResponseEntity<?> unlikeTool(
            @RequestHeader(value = "User-ID", required = false) Integer userId,
            @PathVariable Integer toolId) {

        // 验证登录
        if (userId == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "请先登录");
            return ResponseEntity.status(401).body(error);
        }

        try {
            Map<String, Object> result = toolLikeService.unlikeTool(toolId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "取消点赞成功");
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
     * 获取点赞状态
     * GET /api/tools/{toolId}/like/status
     */
    @GetMapping("/{toolId}/like/status")
    public ResponseEntity<?> getLikeStatus(
            @RequestHeader(value = "User-ID", required = false) Integer userId,
            @PathVariable Integer toolId) {

        Map<String, Object> result = toolLikeService.getLikeStatus(toolId, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", result);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取工具详情（带点赞状态）
     * GET /api/tools/{toolId}
     */
    @GetMapping("/{toolId}")
    public ResponseEntity<?> getToolDetail(
            @RequestHeader(value = "User-ID", required = false) Integer userId,
            @PathVariable Integer toolId) {

        try {
            Map<String, Object> tool = toolLikeService.getToolWithLikeStatus(toolId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", tool);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}