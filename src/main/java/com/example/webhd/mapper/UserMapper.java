package com.example.webhd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.webhd.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.example.webhd.model.Follow;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

import java.util.List;

@Mapper
public interface UserMapper {
    //查询所有用户
    @Select("select  * from t_user ") //mysql语句
    public List<User> find();

    @Select("SELECT COUNT(*) FROM t_user WHERE username = #{username} AND password = #{password}")
    int checkUser(@Param("username") String username, @Param("password") String password);

    @Insert("INSERT INTO t_user (username, password) VALUES (#{username}, #{password})")
    int insertUser(@Param("username") String username, @Param("password") String password);

    @Insert("INSERT INTO users (username, avatar, bio, followers_count, following_count, created_at, updated_at) VALUES (#{username}, 'https://randomuser.me/api/portraits/men/default.jpg','这个用户很懒，还没有填写简介', 0, 0, NOW(), NOW() )")
    int insertUsers(@Param("username") String username);

    // 检查用户名是否已存在
    @Select("SELECT COUNT(*) FROM t_user WHERE username = #{username}")
    int checkUsernameExists(String username);

    /**
     * 根据用户ID获取用户信息
     */
    @Select("SELECT " +
            "    id, " +
            "    username, " +
            "    avatar, " +
            "    bio, " +
            "    followers_count as followersCount, " +
            "    following_count as followingCount, " +
            "    created_at as createdAt " +
            "FROM users " +
            "WHERE id = #{userId}")
    Map<String, Object> getUserById(@Param("userId") Long userId);

    /**
     * 根据用户名获取用户信息
     */
    @Select("SELECT " +
            "    id, " +
            "    username, " +
            "    avatar, " +
            "    bio, " +
            "    followers_count as followersCount, " +
            "    following_count as followingCount, " +
            "    created_at as createdAt " +
            "FROM users " +
            "WHERE username = #{username}")
    Map<String, Object> getUserByUsername(@Param("username") String username);

    /**
     * 更新用户关注者数量
     */
    @Update("UPDATE users SET followers_count = followers_count + 1 WHERE id = #{userId}")
    int incrementFollowersCount(@Param("userId") Long userId);

    /**
     * 减少用户关注者数量
     */
    @Update("UPDATE users SET followers_count = followers_count - 1 WHERE id = #{userId} AND followers_count > 0")
    int decrementFollowersCount(@Param("userId") Long userId);

    /**
     * 更新用户关注数量
     */
    @Update("UPDATE users SET following_count = following_count + 1 WHERE id = #{userId}")
    int incrementFollowingCount(@Param("userId") Long userId);

    /**
     * 减少用户关注数量
     */
    @Update("UPDATE users SET following_count = following_count - 1 WHERE id = #{userId} AND following_count > 0")
    int decrementFollowingCount(@Param("userId") Long userId);

    /**
     * 搜索用户
     */
    @Select("SELECT " +
            "    id, " +
            "    username, " +
            "    avatar, " +
            "    bio, " +
            "    followers_count as followersCount, " +
            "    following_count as followingCount " +
            "FROM users " +
            "WHERE username LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY followers_count DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> searchUsers(@Param("keyword") String keyword,
                                          @Param("limit") int limit);

    /**
     * 检查用户名是否已存在（排除当前用户）
     */
    @Select("SELECT COUNT(*) FROM users WHERE username = #{username} AND id != #{userId}")
    int checkUsernameExists2(@Param("username") String username, @Param("userId") Long userId);

    /**
     * 更新用户信息（同时修改 username, avatar, bio）
     */
    @Update("UPDATE users SET " +
            "    username = #{username}, " +
            "    avatar = #{avatar}, " +
            "    bio = #{bio}, " +
            "    updated_at = NOW() " +
            "WHERE id = #{userId}")
    int updateUserInfo(@Param("userId") Long userId,
                       @Param("username") String username,
                       @Param("avatar") String avatar,
                       @Param("bio") String bio);

}
