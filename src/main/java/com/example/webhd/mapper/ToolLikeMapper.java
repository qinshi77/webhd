package com.example.webhd.mapper;

import org.apache.ibatis.annotations.*;
import java.util.Map;

@Mapper
public interface ToolLikeMapper {

    /**
     * 检查用户是否已点赞
     */
    @Select("SELECT COUNT(*) FROM tool_likes WHERE tool_id = #{toolId} AND user_id = #{userId}")
    int checkLike(@Param("toolId") Integer toolId, @Param("userId") Integer userId);

    /**
     * 添加点赞记录
     */
    @Insert("INSERT INTO tool_likes (tool_id, user_id, created_at) VALUES (#{toolId}, #{userId}, NOW())")
    int insertLike(@Param("toolId") Integer toolId, @Param("userId") Integer userId);

    /**
     * 取消点赞
     */
    @Delete("DELETE FROM tool_likes WHERE tool_id = #{toolId} AND user_id = #{userId}")
    int deleteLike(@Param("toolId") Integer toolId, @Param("userId") Integer userId);

    /**
     * 获取工具的点赞数
     */
    @Select("SELECT COUNT(*) FROM tool_likes WHERE tool_id = #{toolId}")
    int getLikeCount(@Param("toolId") Integer toolId);

    /**
     * 更新工具表的点赞数（如果需要冗余字段）
     */
    @Update("UPDATE kitchen_tools SET likes = (SELECT COUNT(*) FROM tool_likes WHERE tool_id = #{toolId}) WHERE id = #{toolId}")
    int updateToolLikeCount(@Param("toolId") Integer toolId);

    /**
     * 批量检查用户点赞状态
     */
    @Select("<script>" +
            "SELECT tool_id as toolId FROM tool_likes " +
            "WHERE user_id = #{userId} " +
            "AND tool_id IN " +
            "<foreach collection='toolIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    java.util.Set<Integer> getLikedToolIds(@Param("userId") Integer userId,
                                           @Param("toolIds") java.util.List<Integer> toolIds);
}