package com.example.webhd.mapper;

import com.example.webhd.model.TUser;
import org.apache.ibatis.annotations.*;

@Mapper
public interface TUserMapper {

    /**
     * 根据用户ID获取用户信息（不查询 created_at）
     */
    @Select("SELECT id, username, password FROM t_user WHERE id = #{userId}")
    TUser getUserById(@Param("userId") Long userId);

    /**
     * 根据用户名获取用户信息（不查询 created_at）
     */
    @Select("SELECT id, username, password FROM t_user WHERE username = #{username}")
    TUser getUserByUsername(@Param("username") String username);

    /**
     * 更新密码
     */
    @Update("UPDATE t_user SET password = #{newPassword} WHERE id = #{userId}")
    int updatePassword(@Param("userId") Long userId, @Param("newPassword") String newPassword);

    /**
     * 插入新用户
     */
    @Insert("INSERT INTO t_user (username, password) VALUES (#{username}, #{password})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertUser(TUser user);
}