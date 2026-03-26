package com.example.webhd.controller;

import com.example.webhd.dto.KitchenToolDTO;
import com.example.webhd.dto.ToolCreateDTO;
import com.example.webhd.service.KitchenToolService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/kitchen-tools")
@CrossOrigin(origins = "http://localhost:8080")
public class KitchenToolController {

    private final KitchenToolService kitchenToolService;

    public KitchenToolController(KitchenToolService kitchenToolService) {
        this.kitchenToolService = kitchenToolService;
    }

    /**
     * 获取所有厨房工具
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTools(
            @RequestHeader(value = "User-ID", required = false) Integer userId) {
        List<KitchenToolDTO> tools = kitchenToolService.getAllToolsWithComments(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", tools);

        return ResponseEntity.ok(response);
    }

    /**
     * 根据ID获取厨房工具详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getToolById(
            @PathVariable Integer id,
            @RequestHeader(value = "User-ID", required = false) Integer userId) {
        KitchenToolDTO tool = kitchenToolService.getToolById(id, userId);

        if (tool == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "工具不存在");
            return ResponseEntity.badRequest().body(error);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", tool);

        return ResponseEntity.ok(response);
    }

    /**
     * 发布新工具
     * POST /api/kitchen-tools
     */
    @PostMapping
    public ResponseEntity<?> createTool(
            @RequestHeader(value = "User-ID", required = false) Integer userId,
            @Valid @RequestBody ToolCreateDTO createDTO) {

        // 验证登录（只有管理员或登录用户可以发布）
        if (userId == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "请先登录");
            return ResponseEntity.status(401).body(error);
        }

        try {
            KitchenToolDTO newTool = kitchenToolService.createTool(createDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "发布成功");
            response.put("data", newTool);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 更新工具信息
     * PUT /api/kitchen-tools/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTool(
            @RequestHeader(value = "User-ID", required = false) Integer userId,
            @PathVariable Integer id,
            @Valid @RequestBody ToolCreateDTO updateDTO) {

        if (userId == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "请先登录");
            return ResponseEntity.status(401).body(error);
        }

        try {
            KitchenToolDTO updatedTool = kitchenToolService.updateTool(id, updateDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "更新成功");
            response.put("data", updatedTool);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 删除工具
     * DELETE /api/kitchen-tools/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTool(
            @RequestHeader(value = "User-ID", required = false) Integer userId,
            @PathVariable Integer id) {

        if (userId == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "请先登录");
            return ResponseEntity.status(401).body(error);
        }

        try {
            boolean deleted = kitchenToolService.deleteTool(id);

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