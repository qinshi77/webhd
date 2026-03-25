package com.example.webhd.service;

import com.example.webhd.mapper.KitchenToolMapper;
import com.example.webhd.mapper.ToolLikeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ToolLikeService {

    @Autowired
    private ToolLikeMapper toolLikeMapper;

    @Autowired
    private KitchenToolMapper kitchenToolMapper;

    /**
     * 点赞
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> likeTool(Integer toolId, Integer userId) {
        // 1. 验证工具是否存在
        Map<String, Object> tool = kitchenToolMapper.getToolById(toolId);
        if (tool == null) {
            throw new RuntimeException("工具不存在");
        }

        // 2. 检查是否已点赞
        int existing = toolLikeMapper.checkLike(toolId, userId);
        if (existing > 0) {
            throw new RuntimeException("已经点赞过了");
        }

        // 3. 添加点赞记录
        int result = toolLikeMapper.insertLike(toolId, userId);
        if (result <= 0) {
            throw new RuntimeException("点赞失败");
        }

        // 4. 更新工具表的点赞数（可选，如果 kitchen_tools 表有 likes 字段）
        kitchenToolMapper.incrementLikeCount(toolId);

        // 5. 获取最新点赞数
        int likeCount = toolLikeMapper.getLikeCount(toolId);

        // 6. 返回结果
        Map<String, Object> response = new HashMap<>();
        response.put("liked", true);
        response.put("likes", likeCount);
        response.put("toolId", toolId);

        return response;
    }

    /**
     * 取消点赞
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> unlikeTool(Integer toolId, Integer userId) {
        // 1. 验证工具是否存在
        Map<String, Object> tool = kitchenToolMapper.getToolById(toolId);
        if (tool == null) {
            throw new RuntimeException("工具不存在");
        }

        // 2. 检查是否已点赞
        int existing = toolLikeMapper.checkLike(toolId, userId);
        if (existing == 0) {
            throw new RuntimeException("尚未点赞");
        }

        // 3. 删除点赞记录
        int result = toolLikeMapper.deleteLike(toolId, userId);
        if (result <= 0) {
            throw new RuntimeException("取消点赞失败");
        }

        // 4. 更新工具表的点赞数
        kitchenToolMapper.decrementLikeCount(toolId);

        // 5. 获取最新点赞数
        int likeCount = toolLikeMapper.getLikeCount(toolId);

        // 6. 返回结果
        Map<String, Object> response = new HashMap<>();
        response.put("liked", false);
        response.put("likes", likeCount);
        response.put("toolId", toolId);

        return response;
    }

    /**
     * 获取点赞状态
     */
    public Map<String, Object> getLikeStatus(Integer toolId, Integer userId) {
        Map<String, Object> result = new HashMap<>();

        // 获取点赞数
        int likeCount = toolLikeMapper.getLikeCount(toolId);
        result.put("likes", likeCount);

        // 获取当前用户是否点赞
        if (userId != null) {
            int liked = toolLikeMapper.checkLike(toolId, userId);
            result.put("liked", liked > 0);
        } else {
            result.put("liked", false);
        }

        result.put("toolId", toolId);

        return result;
    }

    /**
     * 批量获取工具的点赞状态（用于列表页）
     */
    public Map<Integer, Boolean> getBatchLikeStatus(Integer userId, List<Integer> toolIds) {
        Map<Integer, Boolean> result = new HashMap<>();

        // 初始化所有工具为未点赞
        for (Integer toolId : toolIds) {
            result.put(toolId, false);
        }

        if (userId != null && !toolIds.isEmpty()) {
            Set<Integer> likedToolIds = toolLikeMapper.getLikedToolIds(userId, toolIds);
            for (Integer toolId : likedToolIds) {
                result.put(toolId, true);
            }
        }

        return result;
    }

    /**
     * 获取工具详情（带点赞状态）
     */
    public Map<String, Object> getToolWithLikeStatus(Integer toolId, Integer userId) {
        // 1. 获取工具信息
        Map<String, Object> tool = kitchenToolMapper.getToolById(toolId);
        if (tool == null) {
            throw new RuntimeException("工具不存在");
        }

        // 2. 获取点赞状态
        Map<String, Object> likeStatus = getLikeStatus(toolId, userId);

        // 3. 合并结果
        tool.put("liked", likeStatus.get("liked"));
        tool.put("likesCount", likeStatus.get("likes"));

        return tool;
    }
}