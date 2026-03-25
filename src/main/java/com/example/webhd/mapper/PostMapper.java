package com.example.webhd.mapper;

import com.example.webhd.model.Post;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface PostMapper {

    /**
     * 发布新文章
     */
    @Insert("INSERT INTO posts (content, image, author_id, created_at, likes, updated_at) " +
            "VALUES (#{content}, #{image}, #{authorId}, NOW(), 0, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertPost(Post post);

    /**
     * 查询所有帖子列表（完整版，使用JSON函数）
     */
    @Select("SELECT " +
            "    p.id, " +
            "    p.content, " +
            "    p.image, " +
            "    DATE_FORMAT(p.created_at, '%Y-%m-%d %H:%i') as createdAt, " +
            "    p.likes, " +
            "    JSON_OBJECT( " +
            "        'username', author.username, " +
            "        'avatar', author.avatar " +
            "    ) as author, " +
            "    COALESCE( " +
            "        ( " +
            "            SELECT JSON_ARRAYAGG( " +
            "                JSON_OBJECT( " +
            "                    'author', JSON_OBJECT( " +
            "                        'username', commentAuthor.username, " +
            "                        'avatar', commentAuthor.avatar " +
            "                    ), " +
            "                    'content', c.content, " +
            "                    'createdAt', DATE_FORMAT(c.created_at, '%Y-%m-%d %H:%i') " +
            "                ) " +
            "            ) " +
            "            FROM comments c " +
            "            LEFT JOIN users commentAuthor ON c.author_id = commentAuthor.id " +
            "            WHERE c.post_id = p.id " +
            "            ORDER BY c.created_at ASC " +
            "        ), " +
            "        JSON_ARRAY() " +
            "    ) as comments, " +
            "    0 as liked " +
            "FROM posts p " +
            "LEFT JOIN users author ON p.author_id = author.id " +
            "ORDER BY p.created_at DESC")
    List<Map<String, Object>> getAllPosts();

    /**
     * 查询所有帖子基本信息（简化版，不使用JSON函数）
     */
    /**
     * 查询所有帖子基本信息
     */
    @Select("SELECT " +
            "    p.id, " +
            "    p.content, " +
            "    p.image, " +
            "    DATE_FORMAT(p.created_at, '%Y-%m-%d %H:%i') as createdAt, " +
            "    p.likes, " +
            "    p.author_id, " +  // 添加 author_id 字段
            "    author.username, " +
            "    author.avatar " +
            "FROM posts p " +
            "LEFT JOIN users author ON p.author_id = author.id " +
            "ORDER BY p.created_at DESC")
    List<Map<String, Object>> getPostsBasic();

    /**
     * 获取帖子详情（简化版）
     */
    @Select("SELECT " +
            "    p.id, " +
            "    p.content, " +
            "    p.image, " +
            "    DATE_FORMAT(p.created_at, '%Y-%m-%d %H:%i') as createdAt, " +
            "    p.likes, " +
            "    p.author_id, " +
            "    author.username as authorName, " +
            "    author.avatar as authorAvatar " +
            "FROM posts p " +
            "LEFT JOIN users author ON p.author_id = author.id " +
            "WHERE p.id = #{postId}")
    Map<String, Object> getPostDetailById(@Param("postId") Long postId);

    /**
     * 根据帖子ID查询评论
     */
    @Select("SELECT " +
            "    c.id, " +
            "    c.content, " +
            "    DATE_FORMAT(c.created_at, '%Y-%m-%d %H:%i') as createdAt, " +
            "    commentAuthor.username as authorUsername, " +
            "    commentAuthor.avatar as authorAvatar " +
            "FROM comments c " +
            "LEFT JOIN users commentAuthor ON c.author_id = commentAuthor.id " +
            "WHERE c.post_id = #{postId} " +
            "ORDER BY c.created_at ASC")
    List<Map<String, Object>> getCommentsByPostId(@Param("postId") Long postId);

    /**
     * 获取帖子基本信息（用于验证）
     */
    @Select("SELECT id, author_id, content, likes FROM posts WHERE id = #{postId}")
    Map<String, Object> getPostBasicById(@Param("postId") Long postId);

    /**
     * 删除帖子
     */
    @Delete("DELETE FROM posts WHERE id = #{postId} AND author_id = #{userId}")
    int deletePost(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * 更新帖子
     */
    @Update("UPDATE posts SET content = #{content}, image = #{image}, updated_at = NOW() " +
            "WHERE id = #{id} AND author_id = #{authorId}")
    int updatePost(Post post);

    /**
     * 获取用户的所有帖子
     */
    @Select("SELECT " +
            "    p.id, " +
            "    p.content, " +
            "    p.image, " +
            "    DATE_FORMAT(p.created_at, '%Y-%m-%d %H:%i') as createdAt, " +
            "    p.likes, " +
            "    (SELECT COUNT(*) FROM comments WHERE post_id = p.id) as commentCount, " +
            "    (SELECT COUNT(*) FROM likes WHERE post_id = p.id) as likeCount " +
            "FROM posts p " +
            "WHERE p.author_id = #{userId} " +
            "ORDER BY p.created_at DESC")
    List<Map<String, Object>> getPostsByUserId(@Param("userId") Long userId);

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

    /**
     * 获取帖子点赞数
     */
    @Select("SELECT likes FROM posts WHERE id = #{postId}")
    int getLikeCount(@Param("postId") Long postId);

    /**
     * 搜索帖子（根据关键词）
     */
    @Select("SELECT " +
            "    p.id, " +
            "    p.content, " +
            "    p.image, " +
            "    DATE_FORMAT(p.created_at, '%Y-%m-%d %H:%i') as createdAt, " +
            "    p.likes, " +
            "    author.username, " +
            "    author.avatar " +
            "FROM posts p " +
            "LEFT JOIN users author ON p.author_id = author.id " +
            "WHERE p.content LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY p.created_at DESC")
    List<Map<String, Object>> searchPosts(@Param("keyword") String keyword);

    /**
     * 获取热门帖子（按点赞数排序）
     */
    @Select("SELECT " +
            "    p.id, " +
            "    p.content, " +
            "    p.image, " +
            "    DATE_FORMAT(p.created_at, '%Y-%m-%d %H:%i') as createdAt, " +
            "    p.likes, " +
            "    author.username, " +
            "    author.avatar " +
            "FROM posts p " +
            "LEFT JOIN users author ON p.author_id = author.id " +
            "ORDER BY p.likes DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> getHotPosts(@Param("limit") int limit);

    /**
     * 获取最新帖子（分页）
     */
    @Select("SELECT " +
            "    p.id, " +
            "    p.content, " +
            "    p.image, " +
            "    DATE_FORMAT(p.created_at, '%Y-%m-%d %H:%i') as createdAt, " +
            "    p.likes, " +
            "    author.username, " +
            "    author.avatar " +
            "FROM posts p " +
            "LEFT JOIN users author ON p.author_id = author.id " +
            "ORDER BY p.created_at DESC " +
            "LIMIT #{offset}, #{size}")
    List<Map<String, Object>> getPostsByPage(@Param("offset") int offset, @Param("size") int size);

    /**
     * 获取帖子总数
     */
    @Select("SELECT COUNT(*) FROM posts")
    int getPostCount();
}