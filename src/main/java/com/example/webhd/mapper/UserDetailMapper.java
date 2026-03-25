package com.example.webhd.mapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.Map;

@Mapper
public interface UserDetailMapper {

    /**
     * 根据用户名获取用户信息（包括统计信息）
     */
    @Select("SELECT " +
            "    u.username, " +
            "    u.avatar, " +
            "    u.bio, " +
            "    COALESCE(us.posts_count, 0) as posts, " +
            "    COALESCE(u.followers_count, 0) as followers, " +
            "    COALESCE(u.following_count, 0) as following " +
            "FROM users u " +
            "LEFT JOIN user_stats us ON u.id = us.user_id " +
            "WHERE u.username = #{username}")
    Map<String, Object> getUserInfoByUsername(@Param("username") String username);

    /**
     * 根据用户ID获取用户信息
     */
    @Select("SELECT " +
            "    u.username, " +
            "    u.avatar, " +
            "    u.bio, " +
            "    (SELECT COUNT(*) FROM posts WHERE author_id = u.id) as posts, " +
            "    (SELECT COUNT(*) FROM follows WHERE following_id = u.id) as followers, " +
            "    (SELECT COUNT(*) FROM follows WHERE follower_id = u.id) as following " +
            "FROM users u " +
            "WHERE u.id = #{userId}")
    Map<String, Object> getUserInfoById(@Param("userId") Long userId);

    /**
     * 获取当前登录用户的完整信息
     */
    @Select("SELECT " +
            "    u.username, " +
            "    u.avatar, " +
            "    u.bio, " +
            "    (SELECT COUNT(*) FROM posts WHERE author_id = u.id) as posts, " +
            "    (SELECT COUNT(*) FROM follows WHERE following_id = u.id) as followers, " +
            "    (SELECT COUNT(*) FROM follows WHERE follower_id = u.id) as following " +
            "FROM users u " +
            "WHERE u.id = #{userId}")
    Map<String, Object> getCurrentUserInfo(@Param("userId") Long userId);
}