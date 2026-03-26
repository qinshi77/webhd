package com.example.webhd.service;

import com.example.webhd.dto.KitchenToolDTO;
import com.example.webhd.dto.ToolCreateDTO;
import com.example.webhd.mapper.KitchenToolMapper;
import com.example.webhd.mapper.ToolLikeMapper;
import com.example.webhd.model.KitchenTool;
import com.example.webhd.model.ToolComment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class KitchenToolService {

    private final KitchenToolMapper kitchenToolMapper;
    private final ToolLikeMapper toolLikeMapper;
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public KitchenToolService(KitchenToolMapper kitchenToolMapper,
                              ToolLikeMapper toolLikeMapper) {
        this.kitchenToolMapper = kitchenToolMapper;
        this.toolLikeMapper = toolLikeMapper;
    }

    /**
     * 获取所有厨房工具及其评论（带点赞状态）
     */
    public List<KitchenToolDTO> getAllToolsWithComments(Integer userId) {
        List<KitchenTool> tools = kitchenToolMapper.findAllTools();
        if (CollectionUtils.isEmpty(tools)) {
            return Collections.emptyList();
        }

        List<ToolComment> allComments = kitchenToolMapper.findAllComments();

        final Map<Integer, List<ToolComment>> commentsMap;
        if (CollectionUtils.isEmpty(allComments)) {
            commentsMap = Collections.emptyMap();
        } else {
            commentsMap = allComments.stream()
                    .collect(Collectors.groupingBy(ToolComment::getToolId));
        }

        Map<Integer, Boolean> likeStatusMap = getBatchLikeStatus(userId, tools);

        return tools.stream()
                .map(tool -> convertToDTO(tool, commentsMap.get(tool.getId()), likeStatusMap))
                .collect(Collectors.toList());
    }

    /**
     * 获取所有厨房工具（兼容无参调用）
     */
    public List<KitchenToolDTO> getAllToolsWithComments() {
        return getAllToolsWithComments(null);
    }

    /**
     * 根据工具ID获取详情
     */
    public KitchenToolDTO getToolById(Integer id, Integer userId) {
        KitchenTool tool = kitchenToolMapper.findToolById(id);
        if (tool == null) {
            return null;
        }

        List<ToolComment> comments = kitchenToolMapper.findCommentsByToolId(id);
        Map<Integer, Boolean> likeStatusMap = getBatchLikeStatus(userId, Collections.singletonList(tool));

        return convertToDTO(tool, comments, likeStatusMap);
    }

    /**
     * 发布新工具
     */
    @Transactional(rollbackFor = Exception.class)
    public KitchenToolDTO createTool(ToolCreateDTO createDTO) {
        // 1. 创建工具对象
        KitchenTool tool = new KitchenTool();
        tool.setName(createDTO.getName());
        tool.setDescription(createDTO.getDescription());
        tool.setUse(createDTO.getUse());
        tool.setCuisine(createDTO.getCuisine());
        tool.setUsage(createDTO.getUsage());
        tool.setBuyLink(createDTO.getBuyLink());
        tool.setImage(createDTO.getImage());
        tool.setLikes(0);

        // 2. 插入数据库
        int result = kitchenToolMapper.insertTool(tool);

        if (result <= 0) {
            throw new RuntimeException("发布失败");
        }

        // 3. 返回新创建的工具信息
        Map<Integer, Boolean> emptyLikeMap = new HashMap<>();
        emptyLikeMap.put(tool.getId(), false);

        return convertToDTO(tool, new ArrayList<>(), emptyLikeMap);
    }

    /**
     * 更新工具信息
     */
    @Transactional(rollbackFor = Exception.class)
    public KitchenToolDTO updateTool(Integer id, ToolCreateDTO updateDTO) {
        // 1. 检查工具是否存在
        KitchenTool existingTool = kitchenToolMapper.findToolById(id);
        if (existingTool == null) {
            throw new RuntimeException("工具不存在");
        }

        // 2. 更新工具信息
        KitchenTool tool = new KitchenTool();
        tool.setId(id);
        tool.setName(updateDTO.getName());
        tool.setDescription(updateDTO.getDescription());
        tool.setUse(updateDTO.getUse());
        tool.setCuisine(updateDTO.getCuisine());
        tool.setUsage(updateDTO.getUsage());
        tool.setBuyLink(updateDTO.getBuyLink());
        tool.setImage(updateDTO.getImage());

        int result = kitchenToolMapper.updateTool(tool);

        if (result <= 0) {
            throw new RuntimeException("更新失败");
        }

        // 3. 返回更新后的工具信息
        Map<Integer, Boolean> emptyLikeMap = new HashMap<>();
        emptyLikeMap.put(id, false);

        return convertToDTO(tool, new ArrayList<>(), emptyLikeMap);
    }

    /**
     * 删除工具
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTool(Integer id) {
        // 1. 检查工具是否存在
        KitchenTool tool = kitchenToolMapper.findToolById(id);
        if (tool == null) {
            throw new RuntimeException("工具不存在");
        }

        // 2. 删除工具（评论和点赞会通过外键级联删除）
        int result = kitchenToolMapper.deleteTool(id);

        return result > 0;
    }

    /**
     * 批量获取工具的点赞状态
     */
    private Map<Integer, Boolean> getBatchLikeStatus(Integer userId, List<KitchenTool> tools) {
        Map<Integer, Boolean> likeStatusMap = new HashMap<>();

        for (KitchenTool tool : tools) {
            likeStatusMap.put(tool.getId(), false);
        }

        if (userId == null || CollectionUtils.isEmpty(tools)) {
            return likeStatusMap;
        }

        List<Integer> toolIds = tools.stream()
                .map(KitchenTool::getId)
                .collect(Collectors.toList());

        Set<Integer> likedToolIds = toolLikeMapper.getLikedToolIds(userId, toolIds);

        for (Integer toolId : likedToolIds) {
            likeStatusMap.put(toolId, true);
        }

        return likeStatusMap;
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
        dto.setLiked(likeStatusMap.getOrDefault(tool.getId(), false));

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

        if (comment.getTime() != null) {
            commentDTO.setTime(comment.getTime().format(DATE_TIME_FORMATTER));
        }

        return commentDTO;
    }
}