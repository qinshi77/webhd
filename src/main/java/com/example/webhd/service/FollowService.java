package com.example.webhd.service;

import com.example.webhd.mapper.FollowMapper;
import com.example.webhd.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class FollowService {

    @Autowired
    private FollowMapper followMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 关注用户
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> followUser(Long followerId, Long followingId) {
        // 1. 不能关注自己
        if (followerId.equals(followingId)) {
            throw new RuntimeException("不能关注自己");
        }

        // 2. 检查被关注用户是否存在
        Map<String, Object> targetUser = userMapper.getUserById(followingId);
        if (targetUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 3. 检查是否已关注
        int existing = followMapper.checkFollow(followerId, followingId);
        if (existing > 0) {
            throw new RuntimeException("已经关注过了");
        }

        // 4. 添加关注记录
        int result = followMapper.insertFollow(followerId, followingId);

        if (result > 0) {
            // 5. 更新统计
            userMapper.incrementFollowingCount(followerId);  // 关注者关注数+1
            userMapper.incrementFollowersCount(followingId); // 被关注者粉丝数+1
        }

        // 6. 返回结果
        Map<String, Object> response = new HashMap<>();
        response.put("isFollowing", true);

        // 获取最新的粉丝数
        int followersCount = followMapper.getFollowersCount(followingId);
        response.put("followersCount", followersCount);

        return response;
    }

    /**
     * 取消关注
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> unfollowUser(Long followerId, Long followingId) {
        // 1. 检查是否已关注
        int existing = followMapper.checkFollow(followerId, followingId);
        if (existing == 0) {
            throw new RuntimeException("尚未关注该用户");
        }

        // 2. 删除关注记录
        int result = followMapper.deleteFollow(followerId, followingId);

        if (result > 0) {
            // 3. 更新统计
            userMapper.decrementFollowingCount(followerId);  // 关注者关注数-1
            userMapper.decrementFollowersCount(followingId); // 被关注者粉丝数-1
        }

        // 4. 返回结果
        Map<String, Object> response = new HashMap<>();
        response.put("isFollowing", false);

        // 获取最新的粉丝数
        int followersCount = followMapper.getFollowersCount(followingId);
        response.put("followersCount", followersCount);

        return response;
    }

    /**
     * 检查是否已关注
     */
    public boolean isFollowing(Long followerId, Long followingId) {
        if (followerId == null || followingId == null) {
            return false;
        }
        int count = followMapper.checkFollow(followerId, followingId);
        return count > 0;
    }

    /**
     * 批量检查关注状态
     */
    public Map<Long, Boolean> checkFollowBatch(Long userId, List<Long> userIds) {
        Map<Long, Boolean> result = new HashMap<>();

        if (userId == null || userIds == null || userIds.isEmpty()) {
            for (Long id : userIds) {
                result.put(id, false);
            }
            return result;
        }

        List<Map<String, Object>> followingList = followMapper.checkFollowBatch(userId, userIds);
        Set<Long> followingIds = new HashSet<>();

        for (Map<String, Object> follow : followingList) {
            Long followingId = (Long) follow.get("userId");
            followingIds.add(followingId);
        }

        for (Long id : userIds) {
            result.put(id, followingIds.contains(id));
        }

        return result;
    }

    /**
     * 获取用户的关注列表
     */
    public Map<String, Object> getFollowingList(Long userId, Long currentUserId) {
        List<Map<String, Object>> followingList = followMapper.getFollowingList(userId);

        // 如果当前用户已登录，检查是否关注了列表中的用户
        if (currentUserId != null && !followingList.isEmpty()) {
            List<Long> userIds = new ArrayList<>();
            for (Map<String, Object> user : followingList) {
                userIds.add((Long) user.get("id"));
            }
            Map<Long, Boolean> followStatus = checkFollowBatch(currentUserId, userIds);

            for (Map<String, Object> user : followingList) {
                Long id = (Long) user.get("id");
                user.put("isFollowing", followStatus.getOrDefault(id, false));
            }
        } else {
            for (Map<String, Object> user : followingList) {
                user.put("isFollowing", false);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", followingList);
        result.put("count", followingList.size());

        return result;
    }

    /**
     * 获取用户的粉丝列表
     */
    public Map<String, Object> getFollowersList(Long userId, Long currentUserId) {
        List<Map<String, Object>> followersList = followMapper.getFollowersList(userId);

        // 如果当前用户已登录，检查是否关注了列表中的用户
        if (currentUserId != null && !followersList.isEmpty()) {
            List<Long> userIds = new ArrayList<>();
            for (Map<String, Object> user : followersList) {
                userIds.add((Long) user.get("id"));
            }
            Map<Long, Boolean> followStatus = checkFollowBatch(currentUserId, userIds);

            for (Map<String, Object> user : followersList) {
                Long id = (Long) user.get("id");
                user.put("isFollowing", followStatus.getOrDefault(id, false));
            }
        } else {
            for (Map<String, Object> user : followersList) {
                user.put("isFollowing", false);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", followersList);
        result.put("count", followersList.size());

        return result;
    }

    /**
     * 获取用户的统计数据
     */
    public Map<String, Object> getUserStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();

        int followingCount = followMapper.getFollowingCount(userId);
        int followersCount = followMapper.getFollowersCount(userId);

        stats.put("followingCount", followingCount);
        stats.put("followersCount", followersCount);

        return stats;
    }
}