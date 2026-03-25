package com.example.webhd.service;

import com.example.webhd.mapper.LikeMapper;
import com.example.webhd.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class LikeService {

    @Autowired
    private LikeMapper likeMapper;

    @Autowired
    private PostMapper postMapper;

    /**
     * 点赞
     */
    @Transactional
    public Map<String, Object> likePost(Long postId, Long userId) {
        // 1. 验证帖子是否存在
        Map<String, Object> post = postMapper.getPostBasicById(postId);
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }

        // 2. 检查是否已经点赞
        int existing = likeMapper.checkLike(postId, userId);
        if (existing > 0) {
            throw new RuntimeException("已经点赞过了");
        }

        // 3. 插入点赞记录
        likeMapper.insertLike(postId, userId);

        // 4. 更新帖子点赞数
        likeMapper.incrementLikeCount(postId);

        // 5. 获取最新的点赞数
        int newLikeCount = likeMapper.getLikeCount(postId);

        Map<String, Object> result = new HashMap<>();
        result.put("liked", true);
        result.put("likes", newLikeCount);

        return result;
    }

    /**
     * 取消点赞
     */
    @Transactional
    public Map<String, Object> unlikePost(Long postId, Long userId) {
        // 1. 删除点赞记录
        int deleted = likeMapper.deleteLike(postId, userId);

        if (deleted > 0) {
            // 2. 更新帖子点赞数
            likeMapper.decrementLikeCount(postId);
        }

        // 3. 获取最新的点赞数
        int newLikeCount = likeMapper.getLikeCount(postId);

        Map<String, Object> result = new HashMap<>();
        result.put("liked", false);
        result.put("likes", newLikeCount);

        return result;
    }

    /**
     * 获取点赞状态
     */
    public Map<String, Object> getLikeStatus(Long postId, Long userId) {
        Map<String, Object> result = new HashMap<>();

        if (userId != null) {
            int liked = likeMapper.checkLike(postId, userId);
            result.put("liked", liked > 0);
        } else {
            result.put("liked", false);
        }

        int likeCount = likeMapper.getLikeCount(postId);
        result.put("likes", likeCount);

        return result;
    }
}