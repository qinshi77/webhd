package com.example.webhd.mapper;

import org.apache.ibatis.annotations.*;
import java.util.Map;

@Mapper
public interface LikeMapper {

    /**
     * 检查用户是否点赞过帖子
     */
    @Select("SELECT COUNT(*) FROM likes WHERE post_id = #{postId} AND user_id = #{userId}")
    int checkLike(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * 点赞
     */
    @Insert("INSERT INTO likes (post_id, user_id, created_at) VALUES (#{postId}, #{userId}, NOW())")
    int insertLike(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * 取消点赞
     */
    @Delete("DELETE FROM likes WHERE post_id = #{postId} AND user_id = #{userId}")
    int deleteLike(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * 获取帖子点赞数
     */
    @Select("SELECT COUNT(*) FROM likes WHERE post_id = #{postId}")
    int getLikeCount(@Param("postId") Long postId);

    /**
     * 更新帖子点赞数
     */
    @Update("UPDATE posts SET likes = likes + 1 WHERE id = #{postId}")
    int incrementLikeCount(@Param("postId") Long postId);

    /**
     * 减少帖子点赞数
     */
    @Update("UPDATE posts SET likes = likes - 1 WHERE id = #{postId} AND likes > 0")
    int decrementLikeCount(@Param("postId") Long postId);
}