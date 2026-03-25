package com.example.webhd.service;

import com.example.webhd.dto.KitchenToolDTO;
import com.example.webhd.model.KitchenTool;
import com.example.webhd.model.ToolComment;
import com.example.webhd.mapper.KitchenToolMapper;
import com.example.webhd.mapper.ToolLikeMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class KitchenToolService {

    private final KitchenToolMapper kitchenToolMapper;
    private final ToolLikeMapper toolLikeMapper;  // 注入点赞Mapper
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // 构造器注入
    public KitchenToolService(KitchenToolMapper kitchenToolMapper, ToolLikeMapper toolLikeMapper) {
        this.kitchenToolMapper = kitchenToolMapper;
        this.toolLikeMapper = toolLikeMapper;
    }

    /**
     * 获取所有厨房工具及其评论（带点赞状态）
     * @param userId 当前登录用户ID（可能为null）
     */
    public List<KitchenToolDTO> getAllToolsWithComments(Integer userId) {
        // 查询所有工具
        List<KitchenTool> tools = kitchenToolMapper.findAllTools();
        if (CollectionUtils.isEmpty(tools)) {
            return Collections.emptyList();
        }

        // 查询所有评论
        List<ToolComment> allComments = kitchenToolMapper.findAllComments();

        // 按工具ID分组评论
        final Map<Integer, List<ToolComment>> commentsMap;
        if (CollectionUtils.isEmpty(allComments)) {
            commentsMap = Collections.emptyMap();
        } else {
            commentsMap = allComments.stream()
                    .collect(Collectors.groupingBy(ToolComment::getToolId));
        }

        // 批量获取点赞状态（优化性能，避免循环查询数据库）
        Map<Integer, Boolean> likeStatusMap = getBatchLikeStatus(userId, tools);

        // 组装响应数据
        return tools.stream()
                .map(tool -> convertToDTO(tool, commentsMap.get(tool.getId()), likeStatusMap))
                .collect(Collectors.toList());
    }

    /**
     * 获取所有厨房工具及其评论（兼容无参调用）
     */
    public List<KitchenToolDTO> getAllToolsWithComments() {
        return getAllToolsWithComments(null);
    }

    /**
     * 根据工具ID获取详情（带点赞状态）
     * @param id 工具ID
     * @param userId 当前登录用户ID（可能为null）
     */
    public KitchenToolDTO getToolById(Integer id, Integer userId) {
        // 查询所有工具
        List<KitchenTool> tools = kitchenToolMapper.findAllTools();
        KitchenTool tool = tools.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (tool == null) {
            return null;
        }

        // 查询该工具的评论
        List<ToolComment> comments = kitchenToolMapper.findCommentsByToolId(id);

        // 获取点赞状态
        Map<Integer, Boolean> likeStatusMap = getBatchLikeStatus(userId, Collections.singletonList(tool));

        return convertToDTO(tool, comments, likeStatusMap);
    }

    /**
     * 根据工具ID获取详情（兼容无参调用）
     */
    public KitchenToolDTO getToolById(Integer id) {
        return getToolById(id, null);
    }

    /**
     * 批量获取工具的点赞状态
     * @param userId 当前用户ID
     * @param tools 工具列表
     * @return 工具ID -> 是否点赞的映射
     */
    private Map<Integer, Boolean> getBatchLikeStatus(Integer userId, List<KitchenTool> tools) {
        Map<Integer, Boolean> likeStatusMap = new HashMap<>();

        // 初始化所有工具为未点赞
        for (KitchenTool tool : tools) {
            likeStatusMap.put(tool.getId(), false);
        }

        // 如果用户未登录，返回全 false
        if (userId == null || CollectionUtils.isEmpty(tools)) {
            return likeStatusMap;
        }

        // 获取所有工具ID
        List<Integer> toolIds = tools.stream()
                .map(KitchenTool::getId)
                .collect(Collectors.toList());

        // 批量查询用户点赞的工具ID
        Set<Integer> likedToolIds = toolLikeMapper.getLikedToolIds(userId, toolIds);

        // 更新点赞状态
        for (Integer toolId : likedToolIds) {
            likeStatusMap.put(toolId, true);
        }

        return likeStatusMap;
    }

    /**
     * 获取单个工具的点赞状态
     * @param toolId 工具ID
     * @param userId 当前用户ID
     * @return 是否点赞
     */
    public boolean getLikeStatus(Integer toolId, Integer userId) {
        if (userId == null) {
            return false;
        }
        int count = toolLikeMapper.checkLike(toolId, userId);
        return count > 0;
    }

    /**
     * 转换实体为DTO
     */
    private KitchenToolDTO convertToDTO(KitchenTool tool, List<ToolComment> comments,
                                        Map<Integer, Boolean> likeStatusMap) {
        KitchenToolDTO dto = new KitchenToolDTO();
        dto.setId(tool.getId());
        dto.setName(tool.getName());
        dto.setDescription(tool.getDescription());
        dto.setUse(tool.getUse());
        dto.setCuisine(tool.getCuisine());
        dto.setUsage(tool.getUsage());
        dto.setBuyLink(tool.getBuyLink());
        dto.setImage(tool.getImage());
        dto.setLikes(tool.getLikes());

        // 设置点赞状态（新增字段）
        dto.setLiked(likeStatusMap.getOrDefault(tool.getId(), false));

        // 转换评论
        if (!CollectionUtils.isEmpty(comments)) {
            List<KitchenToolDTO.CommentDTO> commentDTOs = comments.stream()
                    .map(this::convertToCommentDTO)
                    .collect(Collectors.toList());
            dto.setComments(commentDTOs);
        } else {
            dto.setComments(new ArrayList<>());
        }

        return dto;
    }

    /**
     * 转换评论实体为DTO
     */
    private KitchenToolDTO.CommentDTO convertToCommentDTO(ToolComment comment) {
        KitchenToolDTO.CommentDTO commentDTO = new KitchenToolDTO.CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setUser(comment.getUser());
        commentDTO.setAvatar(comment.getAvatar());
        commentDTO.setContent(comment.getContent());

        // 格式化时间
        if (comment.getTime() != null) {
            commentDTO.setTime(comment.getTime().format(DATE_TIME_FORMATTER));
        }

        return commentDTO;
    }
}