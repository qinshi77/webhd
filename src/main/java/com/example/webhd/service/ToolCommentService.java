package com.example.webhd.service;

import com.example.webhd.dto.CommentCreateDTO;
import com.example.webhd.mapper.KitchenToolMapper;
import com.example.webhd.mapper.ToolCommentMapper;
import com.example.webhd.model.ToolComment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;  // 添加这个导入

@Service
public class ToolCommentService {

    private final ToolCommentMapper toolCommentMapper;
    private final KitchenToolMapper kitchenToolMapper;
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ToolCommentService(ToolCommentMapper toolCommentMapper,
                              KitchenToolMapper kitchenToolMapper) {
        this.toolCommentMapper = toolCommentMapper;
        this.kitchenToolMapper = kitchenToolMapper;
    }

    /**
     * 添加评论
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> addComment(Integer toolId,
                                          Integer userId,
                                          String username,
                                          String avatar,
                                          String content) {
        // 1. 验证工具是否存在
        Map<String, Object> tool = kitchenToolMapper.getToolById(toolId);
        if (tool == null) {
            throw new RuntimeException("工具不存在");
        }

        // 2. 验证用户是否登录
        if (userId == null) {
            throw new RuntimeException("请先登录");
        }

        // 3. 验证评论内容
        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("评论内容不能为空");
        }

        // 4. 创建评论对象
        ToolComment comment = new ToolComment();
        comment.setToolId(toolId);
        comment.setUser(username);
        comment.setAvatar(avatar != null ? avatar : "https://randomuser.me/api/portraits/default.jpg");
        comment.setContent(content);
        comment.setTime(LocalDateTime.now());

        // 5. 插入评论
        int result = toolCommentMapper.insertComment(comment);

        if (result <= 0) {
            throw new RuntimeException("评论失败");
        }

        // 6. 获取刚插入的评论
        ToolComment savedComment = toolCommentMapper.getCommentById(comment.getId());

        // 7. 返回结果
        Map<String, Object> response = new HashMap<>();
        response.put("id", savedComment.getId());
        response.put("user", savedComment.getUser());
        response.put("avatar", savedComment.getAvatar());
        response.put("content", savedComment.getContent());
        response.put("time", savedComment.getTime().format(DATE_TIME_FORMATTER));

        return response;
    }

    /**
     * 获取工具的评论列表
     */
    public Map<String, Object> getCommentsByToolId(Integer toolId) {
        // 1. 验证工具是否存在
        Map<String, Object> tool = kitchenToolMapper.getToolById(toolId);
        if (tool == null) {
            throw new RuntimeException("工具不存在");
        }

        // 2. 获取评论列表
        List<ToolComment> comments = toolCommentMapper.getCommentsByToolId(toolId);

        // 3. 格式化评论（修改这里）
        List<Map<String, Object>> formattedComments = new ArrayList<>();
        for (ToolComment comment : comments) {
            formattedComments.add(formatComment(comment));
        }

        // 或者使用 stream 方式（Java 8 兼容）
        // List<Map<String, Object>> formattedComments = comments.stream()
        //         .map(this::formatComment)
        //         .collect(Collectors.toList());  // 改为 collect

        // 4. 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("comments", formattedComments);
        result.put("total", formattedComments.size());

        return result;
    }

    /**
     * 删除评论
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComment(Integer commentId, String userName) {
        // 1. 检查评论是否存在
        ToolComment comment = toolCommentMapper.getCommentById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }

        // 2. 检查是否有权限删除（只有评论作者可以删除）
        if (!comment.getUser().equals(userName)) {
            throw new RuntimeException("只能删除自己的评论");
        }

        // 3. 删除评论
        int result = toolCommentMapper.deleteComment(commentId, userName);

        return result > 0;
    }

    /**
     * 格式化评论
     */
    private Map<String, Object> formatComment(ToolComment comment) {
        Map<String, Object> formatted = new HashMap<>();
        formatted.put("id", comment.getId());
        formatted.put("user", comment.getUser());
        formatted.put("avatar", comment.getAvatar());
        formatted.put("content", comment.getContent());

        // 格式化时间
        if (comment.getTime() != null) {
            formatted.put("time", comment.getTime().format(DATE_TIME_FORMATTER));
        } else {
            formatted.put("time", "");
        }

        return formatted;
    }
}