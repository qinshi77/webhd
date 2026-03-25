package com.example.webhd.controller;

import com.example.webhd.dto.KitchenToolDTO;
import com.example.webhd.service.KitchenToolService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/kitchen-tools")
@CrossOrigin(origins = "*")
public class KitchenToolController {

    private final KitchenToolService kitchenToolService;

    // 构造器注入
    public KitchenToolController(KitchenToolService kitchenToolService) {
        this.kitchenToolService = kitchenToolService;
    }

    /**
     * 获取所有厨房工具及其评论（带点赞状态）
     * GET /api/kitchen-tools
     *
     * @param userId 当前登录用户ID（从Header获取，可选）
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTools(
            @RequestHeader(value = "User-ID", required = false) Integer userId) {

        // 传入userId获取带点赞状态的工具列表
        List<KitchenToolDTO> tools = kitchenToolService.getAllToolsWithComments(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("tools", tools);

        return ResponseEntity.ok(response);
    }

    /**
     * 根据ID获取厨房工具详情（带点赞状态）
     * GET /api/kitchen-tools/{id}
     *
     * @param id 工具ID
     * @param userId 当前登录用户ID（从Header获取，可选）
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getToolById(
            @PathVariable Integer id,
            @RequestHeader(value = "User-ID", required = false) Integer userId) {

        // 传入userId获取带点赞状态的工具详情
        KitchenToolDTO tool = kitchenToolService.getToolById(id, userId);

        if (tool == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("tool", tool);

        return ResponseEntity.ok(response);
    }
}