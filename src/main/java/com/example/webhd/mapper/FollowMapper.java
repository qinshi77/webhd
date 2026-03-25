package com.example.webhd.mapper;

import com.example.webhd.model.Follow;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface FollowMapper {

    /**
     * 关注用户
     */
    @Insert("INSERT INTO follows (follower_id, following_id, created_at) " +
            "VALUES (#{followerId}, #{followingId}, NOW())")
    int insertFollow(@Param("followerId") Long followerId,
                     @Param("followingId") Long followingId);

    /**
     * 取消关注
     */
    @Delete("DELETE FROM follows WHERE follower_id = #{followerId} AND following_id = #{followingId}")
    int deleteFollow(@Param("followerId") Long followerId,
                     @Param("followingId") Long followingId);

    /**
     * 检查是否已关注
     */
    @Select("SELECT COUNT(*) FROM follows WHERE follower_id = #{followerId} AND following_id = #{followingId}")
    int checkFollow(@Param("followerId") Long followerId,
                    @Param("followingId") Long followingId);

    /**
     * 获取用户的关注列表
     */
    @Select("SELECT " +
            "    u.id, " +
            "    u.username, " +
            "    u.avatar, " +
            "    u.bio, " +
            "    u.followers_count as followersCount, " +
            "    u.following_count as followingCount, " +
            "    f.created_at as followTime " +
            "FROM follows f " +
            "LEFT JOIN users u ON f.following_id = u.id " +
            "WHERE f.follower_id = #{userId} " +
            "ORDER BY f.created_at DESC")
    List<Map<String, Object>> getFollowingList(@Param("userId") Long userId);

    /**
     * 获取用户的粉丝列表
     */
    @Select("SELECT " +
            "    u.id, " +
            "    u.username, " +
            "    u.avatar, " +
            "    u.bio, " +
            "    u.followers_count as followersCount, " +
            "    u.following_count as followingCount, " +
            "    f.created_at as followTime " +
            "FROM follows f " +
            "LEFT JOIN users u ON f.follower_id = u.id " +
            "WHERE f.following_id = #{userId} " +
            "ORDER BY f.created_at DESC")
    List<Map<String, Object>> getFollowersList(@Param("userId") Long userId);

    /**
     * 获取关注数量
     */
    @Select("SELECT COUNT(*) FROM follows WHERE follower_id = #{userId}")
    int getFollowingCount(@Param("userId") Long userId);

    /**
     * 获取粉丝数量
     */
    @Select("SELECT COUNT(*) FROM follows WHERE following_id = #{userId}")
    int getFollowersCount(@Param("userId") Long userId);

    /**
     * 批量检查关注状态
     */
    @Select("<script>" +
            "SELECT following_id as userId, 1 as isFollowing " +
            "FROM follows " +
            "WHERE follower_id = #{userId} " +
            "AND following_id IN " +
            "<foreach collection='userIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<Map<String, Object>> checkFollowBatch(@Param("userId") Long userId,
                                               @Param("userIds") List<Long> userIds);
}