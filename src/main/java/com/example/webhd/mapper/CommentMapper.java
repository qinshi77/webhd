package com.example.webhd.mapper;

import com.example.webhd.model.Comment;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface CommentMapper {

    /**
     * 添加评论
     */
    @Insert("INSERT INTO comments (post_id, author_id, content, created_at, updated_at) " +
            "VALUES (#{postId}, #{authorId}, #{content}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertComment(Comment comment);  // 使用实体类

    /**
     * 删除评论
     */
    @Delete("DELETE FROM comments WHERE id = #{commentId} AND author_id = #{userId}")
    int deleteComment(@Param("commentId") Long commentId, @Param("userId") Long userId);

    /**
     * 获取帖子的评论列表
     */
    @Select("SELECT " +
            "    c.id, " +
            "    c.content, " +
            "    DATE_FORMAT(c.created_at, '%Y-%m-%d %H:%i') as createdAt, " +
            "    u.id as userId, " +
            "    u.username, " +
            "    u.avatar " +
            "FROM comments c " +
            "LEFT JOIN users u ON c.author_id = u.id " +
            "WHERE c.post_id = #{postId} " +
            "ORDER BY c.created_at ASC")
    List<Map<String, Object>> getCommentsByPostId(@Param("postId") Long postId);

    /**
     * 获取单条评论详情
     */
    @Select("SELECT " +
            "    c.id, " +
            "    c.content, " +
            "    DATE_FORMAT(c.created_at, '%Y-%m-%d %H:%i') as createdAt, " +
            "    u.id as userId, " +
            "    u.username, " +
            "    u.avatar " +
            "FROM comments c " +
            "LEFT JOIN users u ON c.author_id = u.id " +
            "WHERE c.id = #{commentId}")
    Map<String, Object> getCommentById(@Param("commentId") Long commentId);

    /**
     * 更新帖子的评论数（如果需要）
     * 注意：如果 posts 表没有 comments_count 字段，可以不实现
     */
    @Update("UPDATE posts SET comments_count = comments_count + 1 WHERE id = #{postId}")
    int incrementCommentCount(@Param("postId") Long postId);
}