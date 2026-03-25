package com.example.webhd.controller;

import com.example.webhd.dto.PasswordUpdateDTO;
import com.example.webhd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.webhd.dto.UserUpdateDTO;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:8080")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getCurrentUserInfo(
            @RequestHeader(value = "User-ID", required = false) Long userId) {
        Map<String, Object> userInfo = userService.getCurrentUserInfo(userId);
        return ResponseEntity.ok(userInfo);
    }

    /**
     * 根据用户名获取用户信息
     */
    @GetMapping("/{username}")
    public ResponseEntity<Map<String, Object>> getUserInfo(@PathVariable String username) {
        Map<String, Object> userInfo = userService.getUserInfo(username);
        return ResponseEntity.ok(userInfo);
    }

    /**
     * 更新用户信息（同时修改 username, avatar, bio）
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateUserInfo(
            @RequestHeader(value = "User-ID", required = false) Long userId,
            @Valid @RequestBody UserUpdateDTO updateDTO) {

        // 验证登录
        if (userId == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "请先登录");
            return ResponseEntity.status(401).body(error);
        }

        try {
            Map<String, Object> updatedUser = userService.updateUserInfo(userId, updateDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "更新成功");
            response.put("user", updatedUser);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }


    /**
     * 修改密码
     */
    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(
            @RequestHeader(value = "User-ID", required = false) Long userId,
            @Valid @RequestBody PasswordUpdateDTO passwordDTO) {

        // 验证登录
        if (userId == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "请先登录");
            return ResponseEntity.status(401).body(error);
        }

        try {
            Map<String, Object> result = userService.updatePassword(userId, passwordDTO);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}